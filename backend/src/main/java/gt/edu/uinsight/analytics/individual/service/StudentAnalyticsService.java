package gt.edu.uinsight.analytics.individual.service;

import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.exception.StudentNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StudentAnalyticsService {

    public StudentSummaryResponse getSummary(Long studentId) {

        // Dato simulado temporalmente, mientras A3, A6 y B1 no tienen su API lista.
        // Cuando existan, aquí se reemplazará por las llamadas reales.
        if (studentId == null || studentId <= 0) {
            throw new StudentNotFoundException(studentId);
        }

        String studentCode = "EST-%04d".formatted(studentId);
        BigDecimal studentAverage = new BigDecimal("58.0");
        BigDecimal sectionAverage = new BigDecimal("72.0");
        BigDecimal difference = studentAverage.subtract(sectionAverage);

        return new StudentSummaryResponse(studentCode, studentAverage, sectionAverage, difference);
    }
}