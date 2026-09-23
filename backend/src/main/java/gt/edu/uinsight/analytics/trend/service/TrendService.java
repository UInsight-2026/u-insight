package gt.edu.uinsight.analytics.trend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.entity.Evaluation;
import gt.edu.uinsight.analytics.trend.entity.Grade;
import gt.edu.uinsight.analytics.trend.exception.TrendCalculationException;
import gt.edu.uinsight.analytics.trend.mapper.TrendMapper;
import gt.edu.uinsight.analytics.trend.repository.EvaluationRepository;
import gt.edu.uinsight.analytics.trend.repository.GradeRepository;
import gt.edu.uinsight.analytics.trend.repository.SectionRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Orquesta el cálculo de tendencia: obtiene los datos crudos del
 * repositorio, los normaliza y ordena, delega la clasificación a
 * TrendCalculator y devuelve el DTO final vía TrendMapper.
 */
@Service
public class TrendService {

    private final EvaluationRepository evaluationRepository;
    private final SectionRepository sectionRepository;
    private final GradeRepository gradeRepository;
    private final TrendMapper trendMapper;

    // TODO(B4): estos valores deben venir de la célula C1
    // (indicator_configuration) cuando su API esté disponible.
    // Por ahora son configurables por application.properties para no
    // hardcodear el umbral en el código (regla de negocio 4).
    @Value("${uinsight.trend.negative-threshold:-3}")
    private BigDecimal negativeThreshold;

    @Value("${uinsight.trend.positive-threshold:3}")
    private BigDecimal positiveThreshold;

    public TrendService(EvaluationRepository evaluationRepository, SectionRepository sectionRepository, GradeRepository gradeRepository, TrendMapper trendMapper) {
        this.evaluationRepository = evaluationRepository;
        this.sectionRepository = sectionRepository;
        this.gradeRepository = gradeRepository;
        this.trendMapper = trendMapper;
    }

    public TrendResponse getTrendBySectionId(Long sectionId) {
        // TODO(B4): validar que la sección exista, si no lanzar EntityNotFoundException
        sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Sección con ID " + sectionId + " no encontrada."));

        // Se filtra las evaluaciones de la sección y se ordenan por fecha de evaluación
        List<Evaluation> evaluations = evaluationRepository.findAll().stream()
                .filter(evaluation -> evaluation.getSectionId().equals(sectionId))
                .sorted(Comparator.comparing(Evaluation::getEvaluationDate))
                .toList();

        // Se obtiene todas las calificaciones de la sección
        List<Grade> grades = gradeRepository.findAll().stream()
                .filter(grade -> evaluations.stream()
                        .anyMatch(evaluation -> evaluation.getId().equals(grade.getEvaluationId())))
                .toList();

        // Se construye la serie de promedios por evaluación y se calcula la tendencia
        List<TrendCalculator.ScorePoint> points = buildSectionAverageSeries(grades);
        TrendCalculator.Result result = TrendCalculator.calculate(points, negativeThreshold, positiveThreshold);
        return trendMapper.toTrendResponse(result, points);        
    }

    public TrendResponse getTrendByStudentId(Long studentId) {
        List<Grade> grades = gradeRepository
                .findByStudentIdOrderByEvaluation_EvaluationDateAsc(studentId);

        List<TrendCalculator.ScorePoint> points = buildStudentSeries(grades);
        TrendCalculator.Result result = TrendCalculator.calculate(points, negativeThreshold, positiveThreshold);
        return trendMapper.toTrendResponse(result, points);
    }

    /**
     * Regla de negocio 3: normaliza cada nota a escala 0-100
     * (score / maximumScore * 100) para poder comparar evaluaciones
     * con distinta nota máxima.
     */
    private BigDecimal normalize(Grade grade) {
        Evaluation evaluation = evaluationRepository.findById(grade.getEvaluationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Evaluación con ID " + grade.getEvaluationId() + " no encontrada."));
                        
        if (evaluation == null || evaluation.getMaximumScore() == null
                || evaluation.getMaximumScore() <= 0) {
            throw new TrendCalculationException(
                    "La evaluación " + (evaluation != null ? evaluation.getId() : "desconocida")
                            + " no tiene una nota máxima válida para normalizar la calificación.");
        }
        return grade.getScore() == null ? BigDecimal.ZERO
                : BigDecimal.valueOf(grade.getScore())
                        .divide(BigDecimal.valueOf(evaluation.getMaximumScore()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
    }

    /**
     * si un estudiante no presentó una evaluación
     * simplemente no hay Grade para ella, así que ese punto queda
     * excluido de la serie (nunca se asume un cero).
     */
    private List<TrendCalculator.ScorePoint> buildStudentSeries(List<Grade> grades) {
        List<TrendCalculator.ScorePoint> points = new ArrayList<>();
        int index = 1;
        for (Grade grade : grades) {
            points.add(new TrendCalculator.ScorePoint(
                    "E" + index++, grade.getEvaluationId(), normalize(grade)));
        }
        return points;
    }

    /**
     * agrupa las calificaciones por evaluación,
     * calcula el promedio normalizado de la sección en cada una, y
     * ordena la serie por la fecha real de la evaluación (no por id).
     */
    private List<TrendCalculator.ScorePoint> buildSectionAverageSeries(List<Grade> grades) {
        Map<Long, List<Grade>> gradesByEvaluation = grades.stream()
                .collect(Collectors.groupingBy(Grade::getEvaluationId));

        List<TrendCalculator.ScorePoint> points = new ArrayList<>();
        for (Map.Entry<Long, List<Grade>> entry : gradesByEvaluation.entrySet()) {
            Long evaluationId = entry.getKey();
            List<Grade> evaluationGrades = entry.getValue();

            BigDecimal averageNormalizedScore = evaluationGrades.stream()
                    .map(this::normalize)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(evaluationGrades.size()), 4, RoundingMode.HALF_UP);

            Evaluation evaluation = evaluationRepository.findById(evaluationId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Evaluación con ID " + evaluationId + " no encontrada."));

            points.add(new TrendCalculator.ScorePoint(
                    evaluation.getName(), evaluationId, averageNormalizedScore));
        }

        return points.stream()
                .sorted(Comparator.comparing(p -> {
                    Evaluation evaluation = evaluationRepository.findById(p.evaluationId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Evaluación con ID " + p.evaluationId() + " no encontrada."));
                    return evaluation.getEvaluationDate();
                }))
                .toList();
    }
}
