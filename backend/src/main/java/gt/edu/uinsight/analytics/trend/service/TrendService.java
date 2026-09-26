package gt.edu.uinsight.analytics.trend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.entity.Evaluation;
import gt.edu.uinsight.analytics.trend.entity.Grade;
import gt.edu.uinsight.analytics.trend.exception.TrendCalculationException;
import gt.edu.uinsight.analytics.trend.mapper.TrendMapper;
import gt.edu.uinsight.analytics.trend.repository.EvaluationRepository;
import gt.edu.uinsight.analytics.trend.repository.SectionRepository;
import gt.edu.uinsight.analytics.trend.repository.StudentRepository;
import gt.edu.uinsight.analytics.trend.repository.TrendRepository;
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
    private final StudentRepository studentRepository;
    private final TrendRepository trendRepository;
    private final TrendMapper trendMapper;

    // TODO(B4): estos valores deben venir de la célula C1
    // (indicator_configuration) cuando su API esté disponible.
    // Por ahora son configurables por application.properties para no
    // hardcodear el umbral en el código (regla de negocio 4).
    @Value("${uinsight.trend.negative-threshold:-3}")
    private BigDecimal negativeThreshold;

    @Value("${uinsight.trend.positive-threshold:3}")
    private BigDecimal positiveThreshold;

    public TrendService(EvaluationRepository evaluationRepository, SectionRepository sectionRepository,
            StudentRepository studentRepository, TrendRepository trendRepository, TrendMapper trendMapper) {
        this.evaluationRepository = evaluationRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
        this.trendRepository = trendRepository;
        this.trendMapper = trendMapper;
    }

    public TrendResponse getTrendBySectionId(Long sectionId) {
        sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Sección con ID " + sectionId + " no encontrada."));

        List<Grade> grades = trendRepository.findSectionGradesOrdered(sectionId);

        // Se construye la serie de promedios por evaluación y se calcula la tendencia
        List<TrendCalculator.ScorePoint> points = buildSectionAverageSeries(grades);
        TrendCalculator.Result result = TrendCalculator.calculate(points, negativeThreshold, positiveThreshold);
        return trendMapper.toTrendResponse(result, points);        
    }


    public TrendResponse getTrendByStudentId(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Estudiante con ID " + studentId + " no encontrado."));

        List<Grade> grades = trendRepository.findStudentGradesOrdered(studentId);

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
                        
        if (evaluation.getMaximumScore() == null
                || evaluation.getMaximumScore().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TrendCalculationException(
                    "La evaluación " + evaluation.getId()
                            + " no tiene una nota máxima válida para normalizar la calificación.");
        }
        return grade.getScore() == null ? BigDecimal.ZERO
                : grade.getScore()
                        .divide(evaluation.getMaximumScore(), 4, RoundingMode.HALF_UP)
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
                .collect(java.util.stream.Collectors.groupingBy(Grade::getEvaluationId));

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
