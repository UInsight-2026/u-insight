package gt.edu.uinsight.analytics.individual.service;

import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentTrendResponse;
import gt.edu.uinsight.analytics.individual.exception.StudentNotFoundException;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.service.PositionService;
import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class StudentAnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(StudentAnalyticsService.class);

    private final PositionService positionService;
    private final TrendService trendService;
    private final AnalyticsDatabaseRepository databaseRepository;

    public StudentAnalyticsService(PositionService positionService,
                                   TrendService trendService,
                                   AnalyticsDatabaseRepository databaseRepository) {
        this.positionService = positionService;
        this.trendService = trendService;
        this.databaseRepository = databaseRepository;
    }

    public StudentSummaryResponse getSummary(Long studentId, Long requestedSectionId) {
        log.info("STUDENT_SUMMARY_STARTED studentId={}", studentId);
        validateStudent(studentId);
        Long sectionId = resolveSectionId(studentId, requestedSectionId);
        PerformanceMetrics metrics = calculatePerformanceMetrics(studentId, sectionId);
        return new StudentSummaryResponse(studentCode(studentId),
                metrics.studentAverage(), metrics.sectionAverage(), metrics.difference());
    }

    public StudentComparisonResponse getComparison(Long studentId, Long requestedSectionId) {
        log.info("STUDENT_COMPARISON_STARTED studentId={}", studentId);
        validateStudent(studentId);
        Long sectionId = resolveSectionId(studentId, requestedSectionId);
        PerformanceMetrics metrics = calculatePerformanceMetrics(studentId, sectionId);

        StudentPositionResponse position = positionService.getStudentPosition(studentId, sectionId);
        return new StudentComparisonResponse(studentCode(studentId),
                metrics.studentAverage(), metrics.sectionAverage(), metrics.difference(),
                position.getPercentile());
    }

    public StudentTrendResponse getTrend(Long studentId) {
        log.info("STUDENT_TREND_STARTED studentId={}", studentId);
        validateStudent(studentId);

        TrendResponse trend = trendService.getTrendByStudentId(studentId);
        if (trend == null || trend.classification() == null) {
            return new StudentTrendResponse(studentCode(studentId), "INSUFFICIENT_DATA", null);
        }
        Double averageChange = trend.averageChange() == null ? null : trend.averageChange().doubleValue();
        return new StudentTrendResponse(studentCode(studentId), trend.classification().name(), averageChange);
    }

    private void validateStudent(Long studentId) {
        if (studentId == null || studentId <= 0 || !databaseRepository.studentExists(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
    }

    private String studentCode(Long studentId) {
        String studentCode = databaseRepository.findStudentCode(studentId);
        if (studentCode == null || studentCode.isBlank()) {
            throw new IllegalStateException("El estudiante no tiene un código registrado en la base de datos.");
        }
        return studentCode;
    }

    private Long resolveSectionId(Long studentId, Long requestedSectionId) {
        List<Long> sectionIds = databaseRepository.findActiveSectionIdsByStudent(studentId);
        if (requestedSectionId != null) {
            if (!sectionIds.contains(requestedSectionId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El estudiante no tiene una inscripción activa en la sección " + requestedSectionId + ".");
            }
            return requestedSectionId;
        }
        if (sectionIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El estudiante no tiene una inscripción activa en ninguna sección.");
        }
        if (sectionIds.size() > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El estudiante tiene varias secciones activas; debe indicar el parámetro sectionId.");
        }
        return sectionIds.get(0);
    }

    private PerformanceMetrics calculatePerformanceMetrics(Long studentId, Long sectionId) {
        BigDecimal studentAverage = average(
                databaseRepository.findGradesByStudentAndSection(studentId, sectionId).stream()
                        .map(AnalyticsDatabaseRepository.AnalyticsGrade::score)
                        .toList());
        BigDecimal sectionAverage = average(
                databaseRepository.findGradesBySection(sectionId).stream()
                        .map(AnalyticsDatabaseRepository.AnalyticsGrade::score)
                        .toList());

        if (studentAverage == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "El estudiante no tiene calificaciones registradas en la sección " + sectionId + ".");
        }
        if (sectionAverage == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "La sección no tiene calificaciones registradas.");
        }
        return new PerformanceMetrics(studentAverage, sectionAverage, studentAverage.subtract(sectionAverage));
    }

    private BigDecimal average(List<BigDecimal> scores) {
        if (scores.isEmpty()) {
            return null;
        }
        BigDecimal sum = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(scores.size()), 1, RoundingMode.HALF_UP);
    }

    private record PerformanceMetrics(
            BigDecimal studentAverage,
            BigDecimal sectionAverage,
            BigDecimal difference) {
    }
}
