package gt.edu.uinsight.analytics.trend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository.AnalyticsGrade;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.exception.TrendCalculationException;
import gt.edu.uinsight.analytics.trend.mapper.TrendMapper;
import gt.edu.uinsight.evaluation.api.EvaluationSummary;
import jakarta.persistence.EntityNotFoundException;


/**
 * Orquesta el cálculo de tendencia: obtiene los datos crudos del
 * repositorio, los normaliza y ordena, delega la clasificación a
 * TrendCalculator y devuelve el DTO final vía TrendMapper.
 */
@Service
public class TrendService {

    private static final Logger log = LoggerFactory.getLogger(TrendService.class);
    private final AnalyticsDatabaseRepository analyticsRepository;
    private final TrendMapper trendMapper;

    // TODO(B4): estos valores deben venir de la célula C1
    // (indicator_configuration) cuando su API esté disponible.
    // Por ahora son configurables por application.properties para no
    // hardcodear el umbral en el código (regla de negocio 4).
    @Value("${uinsight.trend.negative-threshold:-3}")
    private BigDecimal negativeThreshold;

    @Value("${uinsight.trend.positive-threshold:3}")
    private BigDecimal positiveThreshold;

    public TrendService(TrendMapper trendMapper, AnalyticsDatabaseRepository analyticsRepository) {
        this.trendMapper = trendMapper;
        this.analyticsRepository = analyticsRepository;
    }

    public TrendResponse getTrendBySectionId(Long sectionId) {
        log.info("TREND_CALCULATION_STARTED scope=SECTION sectionId={}", sectionId);

        if (!analyticsRepository.sectionExists(sectionId)) {
            throw new EntityNotFoundException("La sección con id " + sectionId + " no existe.");
        }

        List<AnalyticsGrade> grades = analyticsRepository.findGradesBySection(sectionId);
        if(grades.isEmpty()){
            return new TrendResponse(TrendClassification.INSUFFICIENT_DATA, null, null);
        }
        try {
            Map<Long, EvaluationSummary> evaluations = loadEvaluations(grades);
            List<TrendCalculator.ScorePoint> points = buildSectionAverageSeries(grades, evaluations);
            TrendCalculator.Result result = TrendCalculator.calculate(points, negativeThreshold, positiveThreshold);

            if (result.classification() == TrendClassification.INSUFFICIENT_DATA) {
                log.warn("TREND_INSUFFICIENT_DATA scope=SECTION sectionId={} evaluacionesEncontradas={}",
                        sectionId, points.size());
            } else {
                log.info("TREND_CALCULATION_SUCCESS scope=SECTION sectionId={} classification={} averageChange={}",
                        sectionId, result.classification(), result.averageChange());
            }

            return trendMapper.toTrendResponse(result, points);
        } catch (TrendCalculationException ex) {
            log.error("TREND_CALCULATION_ERROR scope=SECTION sectionId={}", sectionId, ex);
            throw ex;
        }
    }

    public TrendResponse getTrendByStudentId(Long studentId) {
        log.info("TREND_CALCULATION_STARTED scope=STUDENT studentId={}", studentId);

        if (!analyticsRepository.studentExists(studentId)) {
            throw new EntityNotFoundException("El estudiante con id " + studentId + " no existe.");
        }

        List<AnalyticsGrade> grades = analyticsRepository.findGradesByStudent(studentId);
        if(grades.isEmpty()){
            return new TrendResponse(TrendClassification.INSUFFICIENT_DATA, null, null);
        }
        try {
            Map<Long, EvaluationSummary> evaluations = loadEvaluations(grades);
            List<TrendCalculator.ScorePoint> points = buildStudentSeries(grades, evaluations);
            TrendCalculator.Result result = TrendCalculator.calculate(
                    points, negativeThreshold, positiveThreshold);

            if (result.classification() == TrendClassification.INSUFFICIENT_DATA) {
                log.warn("TREND_INSUFFICIENT_DATA scope=STUDENT studentId={} evaluacionesEncontradas={}",
                        studentId, points.size());
            } else {
                log.info("TREND_CALCULATION_SUCCESS scope=STUDENT studentId={} classification={} averageChange={}",
                        studentId, result.classification(), result.averageChange());
            }

            return trendMapper.toTrendResponse(result, points);
        } catch (TrendCalculationException ex) {
            log.error("TREND_CALCULATION_ERROR scope=STUDENT studentId={}", studentId, ex);
            throw ex;
        }
    }

    /**
     * Regla de negocio 3: normaliza cada nota a escala 0-100
     * (score / maximumScore * 100) para poder comparar evaluaciones
     * con distinta nota máxima.
     */
    private BigDecimal normalize(AnalyticsGrade grade, Map<Long, EvaluationSummary> evaluations) {
        EvaluationSummary evaluation = evaluations.get(grade.evaluationId());
        if (evaluation == null) {
            throw new EntityNotFoundException(
                    "No existe la evaluación con id " + grade.evaluationId() + ".");
        }

        if (evaluation.maximumScore() == null
                || evaluation.maximumScore().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TrendCalculationException(
                    "La evaluación " + evaluation.id()
                            + " no tiene una nota máxima válida para normalizar la calificación.");
        }
        return grade.score() == null ? BigDecimal.ZERO
                : grade.score()
                        .divide(evaluation.maximumScore(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
    }

    private Map<Long, EvaluationSummary> loadEvaluations(List<AnalyticsGrade> grades) {
        List<Long> evaluationIds = grades.stream()
                .map(AnalyticsGrade::evaluationId)
                .distinct()
                .toList();

        return evaluationIds.stream()
                .map(analyticsRepository::findEvaluationSummary)
                .flatMap(java.util.Optional::stream)
                .collect(Collectors.toMap(EvaluationSummary::id, evaluation -> evaluation));
    }

    /**
     * si un estudiante no presentó una evaluación
     * simplemente no hay Grade para ella, así que ese punto queda
     * excluido de la serie (nunca se asume un cero).
     */
    private List<TrendCalculator.ScorePoint> buildStudentSeries(
            List<AnalyticsGrade> grades,
            Map<Long, EvaluationSummary> evaluations) {
        return IntStream.range(0, grades.size())
                .mapToObj(index -> {
                    AnalyticsGrade grade = grades.get(index);
                    return new TrendCalculator.ScorePoint(
                            "E" + (index + 1), grade.evaluationId(), normalize(grade, evaluations));
                })
                .toList();
    }

    /**
     * agrupa las calificaciones por evaluación,
     * calcula el promedio normalizado de la sección en cada una, y
     * ordena la serie por la fecha real de la evaluación (no por id).
     */
    private List<TrendCalculator.ScorePoint> buildSectionAverageSeries(
            List<AnalyticsGrade> grades,
            Map<Long, EvaluationSummary> evaluations) {
        Map<Long, List<AnalyticsGrade>> gradesByEvaluation = grades.stream()
                .collect(Collectors.groupingBy(AnalyticsGrade::evaluationId));
        List<Map.Entry<Long, List<AnalyticsGrade>>> orderedEvaluations = gradesByEvaluation.entrySet().stream()
                .sorted(Comparator.comparing(entry ->
                        evaluations.get(entry.getKey()).evaluationDate(),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        return IntStream.range(0, orderedEvaluations.size())
                .mapToObj(index -> {
                    Map.Entry<Long, List<AnalyticsGrade>> entry = orderedEvaluations.get(index);
                    BigDecimal averageNormalizedScore = entry.getValue().stream()
                            .map(grade -> normalize(grade, evaluations))
                            .reduce(BigDecimal.ZERO, (total, score) -> total.add(score))
                            .divide(BigDecimal.valueOf(entry.getValue().size()), 4, RoundingMode.HALF_UP);

                    return new TrendCalculator.ScorePoint(
                            "E" + (index + 1), entry.getKey(), averageNormalizedScore);
                })
                .toList();
    }
}