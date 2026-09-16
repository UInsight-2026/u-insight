package gt.edu.uinsight.academicperiod.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateAcademicPeriodRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull @Positive Integer year,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}
