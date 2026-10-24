package gt.edu.uinsight.academicperiod.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Datos editables de un periodo. El anio y el estado no se cambian por esta via:
 * el estado solo cambia con PATCH /{id}/status.
 */
public record UpdateAcademicPeriodRequest(
        @Schema(example = "Primer Semestre") @NotBlank @Size(max = 100) String name,
        @Schema(example = "2026-01-20") @NotNull LocalDate startDate,
        @Schema(example = "2026-06-05") @NotNull LocalDate endDate
) {
}
