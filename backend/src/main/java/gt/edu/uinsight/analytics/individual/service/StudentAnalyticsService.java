package gt.edu.uinsight.analytics.individual.service;

import gt.edu.uinsight.analytics.centraltendency.service.CentralTendencyService;
import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.exception.StudentNotFoundException;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentTrendResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StudentAnalyticsService {

    private final CentralTendencyService centralTendencyService;

    public StudentAnalyticsService(CentralTendencyService centralTendencyService) {
        this.centralTendencyService = centralTendencyService;
    }


    public StudentSummaryResponse getSummary(Long studentId) {

        // Dato simulado temporalmente, mientras A3, A6 y B1 no tienen su API lista.
        // Cuando existan, aquí se reemplazará por las llamadas reales.
        if (studentId == null || studentId <= 0) {
            throw new StudentNotFoundException(studentId);
        }

        String studentCode = "EST-%04d".formatted(studentId);
        BigDecimal studentAverage = new BigDecimal("58.0");

        CentralTendencyResponse centralTendency = centralTendencyService.getSectionCentralTendency(10L, null);
        BigDecimal sectionAverage = centralTendency.mean() != null
                ? BigDecimal.valueOf(centralTendency.mean())
                : new BigDecimal("72.0");

        BigDecimal difference = studentAverage.subtract(sectionAverage);

        return new StudentSummaryResponse(studentCode, studentAverage, sectionAverage, difference);
    }

public StudentComparisonResponse getComparison(Long studentId) {

    if (studentId == null || studentId <= 0) {
        throw new StudentNotFoundException(studentId);
    }

    String studentCode = "EST-%04d".formatted(studentId);
    BigDecimal studentAverage = new BigDecimal("58.0");

    CentralTendencyResponse centralTendency = centralTendencyService.getSectionCentralTendency(10L, null);
    BigDecimal sectionAverage = centralTendency.mean() != null
            ? BigDecimal.valueOf(centralTendency.mean())
            : new BigDecimal("72.0");

    BigDecimal difference = studentAverage.subtract(sectionAverage);
    Integer percentile = 20;

    return new StudentComparisonResponse(studentCode, studentAverage, sectionAverage, difference, percentile);
}


     public StudentTrendResponse getTrend(Long studentId) {

        if (studentId == null || studentId <= 0) {
            throw new StudentNotFoundException(studentId);
        }

        String studentCode = "EST-%04d".formatted(studentId);
        String trend = "NEGATIVE";
        Double averageChange = -5.0;

        return new StudentTrendResponse(studentCode, trend, averageChange);
    }

}