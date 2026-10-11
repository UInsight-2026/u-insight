package gt.edu.uinsight.analytics.individual.service;

import gt.edu.uinsight.analytics.centraltendency.service.CentralTendencyService;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentTrendResponse;
import gt.edu.uinsight.analytics.individual.exception.StudentNotFoundException;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.service.PositionService;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendService;
import gt.edu.uinsight.enrollment.entity.Enrollment;
import gt.edu.uinsight.enrollment.repository.EnrollmentRepository;
import gt.edu.uinsight.grade.dto.GradeResponse;
import gt.edu.uinsight.grade.service.GradeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class StudentAnalyticsService {

    private static final BigDecimal FALLBACK_SECTION_AVERAGE = new BigDecimal("72.0");
    private static final int FALLBACK_PERCENTILE = 20;

    private final CentralTendencyService centralTendencyService;
    private final PositionService positionService;
    private final GradeService gradeService;
    private final TrendService trendService;
    private final EnrollmentRepository enrollmentRepository;

    public StudentAnalyticsService(CentralTendencyService centralTendencyService,
                                   PositionService positionService,
                                   GradeService gradeService,
                                   TrendService trendService,
                                   EnrollmentRepository enrollmentRepository) {
        this.centralTendencyService = centralTendencyService;
        this.positionService = positionService;
        this.gradeService = gradeService;
        this.trendService = trendService;
        this.enrollmentRepository = enrollmentRepository;
    }

    public StudentSummaryResponse getSummary(Long studentId) {
        validateId(studentId);

        BigDecimal studentAverage = calculateStudentAverage(studentId);
        BigDecimal sectionAverage = resolveSectionAverage(studentId);
        BigDecimal difference = studentAverage.subtract(sectionAverage);

        return new StudentSummaryResponse(buildStudentCode(studentId), studentAverage, sectionAverage, difference);
    }

    public StudentComparisonResponse getComparison(Long studentId) {
        validateId(studentId);

        BigDecimal studentAverage = calculateStudentAverage(studentId);
        BigDecimal sectionAverage = resolveSectionAverage(studentId);
        BigDecimal difference = studentAverage.subtract(sectionAverage);

        Integer percentile;
        try {
            StudentPositionResponse position = positionService.getStudentPosition(studentId);
            percentile = position.getPercentile();
        } catch (RuntimeException e) {
            // Fallback: B2 no pudo calcular la posicion del estudiante
            percentile = FALLBACK_PERCENTILE;
        }

        return new StudentComparisonResponse(buildStudentCode(studentId), studentAverage,
                sectionAverage, difference, percentile);
    }

    public StudentTrendResponse getTrend(Long studentId) {
        validateId(studentId);

        TrendResponse trend;
        try {
            trend = trendService.getTrendByStudentId(studentId);
        } catch (RuntimeException e) {
            // Fallback: B4 no pudo calcular la tendencia
            trend = null;
        }

        String studentCode = buildStudentCode(studentId);
        if (trend == null || trend.classification() == null) {
            return new StudentTrendResponse(studentCode, "INSUFFICIENT_DATA", null);
        }

        Double averageChange = trend.averageChange() != null ? trend.averageChange().doubleValue() : null;
        return new StudentTrendResponse(studentCode, trend.classification().name(), averageChange);
    }

    private void validateId(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new StudentNotFoundException(studentId);
        }
    }

    private String buildStudentCode(Long studentId) {
        return "EST-%04d".formatted(studentId);
    }

    // Promedio de los score crudos de A6 (mismo criterio que B1 para la seccion)
    private BigDecimal calculateStudentAverage(Long studentId) {
        List<BigDecimal> scores = gradeService.getByStudent(studentId).stream()
                .map(GradeResponse::getScore)
                .filter(Objects::nonNull)
                .toList();

        if (scores.isEmpty()) {
            throw new StudentNotFoundException(studentId);
        }

        BigDecimal sum = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(scores.size()), 1, RoundingMode.HALF_UP);
    }

    // Promedio de la seccion del estudiante segun B1, con fallback si no hay inscripcion o B1 falla
    private BigDecimal resolveSectionAverage(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        if (enrollments.isEmpty()) {
            return FALLBACK_SECTION_AVERAGE;
        }

        Long sectionId = enrollments.get(0).getSectionId();
        try {
            Double mean = centralTendencyService.getSectionCentralTendency(sectionId, null).mean();
            return mean != null ? BigDecimal.valueOf(mean) : FALLBACK_SECTION_AVERAGE;
        } catch (RuntimeException e) {
            return FALLBACK_SECTION_AVERAGE;
        }
    }
}
