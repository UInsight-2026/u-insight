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
import gt.edu.uinsight.analytics.trend.entity.EvaluationRecord;
import gt.edu.uinsight.analytics.trend.entity.GradeRecord;
import gt.edu.uinsight.analytics.trend.exception.TrendCalculationException;
import gt.edu.uinsight.analytics.trend.mapper.TrendMapper;
import gt.edu.uinsight.analytics.trend.repository.TrendRepository;

/**
 * Orquesta el cálculo de tendencia: obtiene los datos crudos del
 * repositorio, los normaliza y ordena, delega la clasificación a
 * TrendCalculator y devuelve el DTO final vía TrendMapper.
 */
@Service
public class TrendService {

    private final TrendRepository trendRepository;

    // TODO(B4): estos valores deben venir de la célula C1
    // (indicator_configuration) cuando su API esté disponible.
    // Por ahora son configurables por application.properties para no
    // hardcodear el umbral en el código (regla de negocio 4).
    @Value("${uinsight.trend.negative-threshold:-3}")
    private BigDecimal negativeThreshold;

    @Value("${uinsight.trend.positive-threshold:3}")
    private BigDecimal positiveThreshold;

    public TrendService(TrendRepository trendRepository) {
        this.trendRepository = trendRepository;
    }

    public TrendResponse getTrendBySectionId(Long sectionId) {
        List<GradeRecord> grades = trendRepository
                .findByEvaluation_SectionIdOrderByEvaluation_EvaluationDateAsc(sectionId);

        List<TrendCalculator.ScorePoint> points = buildSectionAverageSeries(grades);
        TrendCalculator.Result result = TrendCalculator.calculate(points, negativeThreshold, positiveThreshold);
        return TrendMapper.toTrendResponse(result, points);
    }

    public TrendResponse getTrendByStudentId(Long studentId) {
        List<GradeRecord> grades = trendRepository
                .findByStudentIdOrderByEvaluation_EvaluationDateAsc(studentId);

        List<TrendCalculator.ScorePoint> points = buildStudentSeries(grades);
        TrendCalculator.Result result = TrendCalculator.calculate(points, negativeThreshold, positiveThreshold);
        return TrendMapper.toTrendResponse(result, points);
    }

    /**
     * Regla de negocio 3: normaliza cada nota a escala 0-100
     * (score / maximumScore * 100) para poder comparar evaluaciones
     * con distinta nota máxima.
     */
    private BigDecimal normalize(GradeRecord grade) {
        EvaluationRecord evaluation = grade.getEvaluation();
        if (evaluation == null || evaluation.getMaximumScore() == null
                || evaluation.getMaximumScore().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TrendCalculationException(
                    "La evaluación " + (evaluation != null ? evaluation.getId() : "desconocida")
                            + " no tiene una nota máxima válida para normalizar la calificación.");
        }
        return grade.getScore()
                .divide(evaluation.getMaximumScore(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * si un estudiante no presentó una evaluación
     * simplemente no hay GradeRecord para ella, así que ese punto queda
     * excluido de la serie (nunca se asume un cero).
     */
    private List<TrendCalculator.ScorePoint> buildStudentSeries(List<GradeRecord> grades) {
        List<TrendCalculator.ScorePoint> points = new ArrayList<>();
        int index = 1;
        for (GradeRecord grade : grades) {
            points.add(new TrendCalculator.ScorePoint(
                    "E" + index++, grade.getEvaluation().getId(), normalize(grade)));
        }
        return points;
    }

    /**
     * agrupa las calificaciones por evaluación,
     * calcula el promedio normalizado de la sección en cada una, y
     * ordena la serie por la fecha real de la evaluación (no por id).
     */
    private List<TrendCalculator.ScorePoint> buildSectionAverageSeries(List<GradeRecord> grades) {
        Map<Long, List<GradeRecord>> byEvaluationId = grades.stream()
                .collect(Collectors.groupingBy(g -> g.getEvaluation().getId()));

        return byEvaluationId.values().stream()
                .sorted(Comparator.comparing(list -> list.get(0).getEvaluation().getEvaluationDate()))
                .map(evaluationGrades -> {
                    EvaluationRecord evaluation = evaluationGrades.get(0).getEvaluation();
                    BigDecimal average = evaluationGrades.stream()
                            .map(this::normalize)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(evaluationGrades.size()), 2, RoundingMode.HALF_UP);
                    return new TrendCalculator.ScorePoint(evaluation.getName(), evaluation.getId(), average);
                })
                .toList();
    }
}
