package gt.edu.uinsight.academicperiod.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateAcademicPeriodRequest(
        @Schema(example = "Primer Semestre") @NotBlank @Size(max = 100) String name,
        @Schema(example = "2026") @NotNull @Positive Integer year,
        @Schema(example = "2026-01-15") @NotNull LocalDate startDate,
        @Schema(example = "2026-05-30") @NotNull LocalDate endDate
) {
}
