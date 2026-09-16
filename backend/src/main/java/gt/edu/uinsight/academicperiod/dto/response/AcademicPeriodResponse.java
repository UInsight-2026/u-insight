package gt.edu.uinsight.academicperiod.dto.response;

import gt.edu.uinsight.academicperiod.entity.PeriodStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AcademicPeriodResponse(
        Long id,
        String name,
        Integer year,
        LocalDate startDate,
        LocalDate endDate,
        PeriodStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
