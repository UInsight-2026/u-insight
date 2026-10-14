package gt.edu.uinsight.analytics.trend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import gt.edu.uinsight.analytics.trend.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;


/**
 * Orquesta el cálculo de tendencia: obtiene los datos crudos del
 * repositorio, los normaliza y ordena, delega la clasificación a
 * TrendCalculator y devuelve el DTO final vía TrendMapper.
 */
@Service
public class TrendService {

    private static final Logger log = LoggerFactory.getLogger(TrendService.class);
    private final GradeRepository gradeRepository;
    private final TrendMapper trendMapper;
    private final EvaluationRepository evaluationRepository;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;

    // TODO(B4): estos valores deben venir de la célula C1
    // (indicator_configuration) cuando su API esté disponible.
    // Por ahora son configurables por application.properties para no
    // hardcodear el umbral en el código (regla de negocio 4).
    @Value("${uinsight.trend.negative-threshold:-3}")
    private BigDecimal negativeThreshold;

    @Value("${uinsight.trend.positive-threshold:3}")
    private BigDecimal positiveThreshold;

    public TrendService( TrendMapper trendMapper, EvaluationRepository evaluationRepository,
                         GradeRepository gradeRepository, SectionRepository sectionRepository,
                         StudentRepository studentRepository) {
        this.trendMapper = trendMapper;
        this.evaluationRepository = evaluationRepository;
        this.gradeRepository = gradeRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
    }

    public TrendResponse getTrendBySectionId(Long sectionId) {
        log.info("TREND_CALCULATION_STARTED scope=SECTION sectionId={}", sectionId);

        sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "La sección con id " + sectionId + " no existe."));

        List<Grade> grades = gradeRepository.findBySectionId(sectionId);
        if(grades.isEmpty()){
            return new TrendResponse(TrendClassification.INSUFFICIENT_DATA, null, null);
        }
        try {
            Map<Long, Evaluation> evaluations = loadEvaluations(grades);
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

        studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El estudiante con id " + studentId + " no existe."));

        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        if(grades.isEmpty()){
            return new TrendResponse(TrendClassification.INSUFFICIENT_DATA, null, null);
        }
        try {
            Map<Long, Evaluation> evaluations = loadEvaluations(grades);
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
    private BigDecimal normalize(Grade grade, Map<Long, Evaluation> evaluations) {
        Evaluation evaluation = evaluations.get(grade.getEvaluationId());
        if (evaluation == null) {
            throw new EntityNotFoundException(
                    "No existe la evaluación con id " + grade.getEvaluationId() + ".");
        }

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

    private Map<Long, Evaluation> loadEvaluations(List<Grade> grades) {
        List<Long> evaluationIds = grades.stream()
                .map(grade -> grade.getEvaluationId())
                .distinct()
                .toList();

        return StreamSupport.stream(evaluationRepository.findAllById(evaluationIds).spliterator(), false)
                .collect(Collectors.toMap(evaluation -> evaluation.getId(), evaluation -> evaluation));
    }

    /**
     * si un estudiante no presentó una evaluación
     * simplemente no hay Grade para ella, así que ese punto queda
     * excluido de la serie (nunca se asume un cero).
     */
    private List<TrendCalculator.ScorePoint> buildStudentSeries(
            List<Grade> grades,
            Map<Long, Evaluation> evaluations) {
        return IntStream.range(0, grades.size())
                .mapToObj(index -> {
                    Grade grade = grades.get(index);
                    return new TrendCalculator.ScorePoint(
                            "E" + (index + 1), grade.getEvaluationId(), normalize(grade, evaluations));
                })
                .toList();
    }

    /**
     * agrupa las calificaciones por evaluación,
     * calcula el promedio normalizado de la sección en cada una, y
     * ordena la serie por la fecha real de la evaluación (no por id).
     */
    private List<TrendCalculator.ScorePoint> buildSectionAverageSeries(
            List<Grade> grades,
            Map<Long, Evaluation> evaluations) {
        Map<Long, List<Grade>> gradesByEvaluation = grades.stream()
                .collect(Collectors.groupingBy(grade -> grade.getEvaluationId()));
        List<Map.Entry<Long, List<Grade>>> orderedEvaluations = gradesByEvaluation.entrySet().stream()
                .sorted(Comparator.comparing(entry ->
                        evaluations.get(entry.getKey()).getEvaluationDate()))
                .toList();

        return IntStream.range(0, orderedEvaluations.size())
                .mapToObj(index -> {
                    Map.Entry<Long, List<Grade>> entry = orderedEvaluations.get(index);
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