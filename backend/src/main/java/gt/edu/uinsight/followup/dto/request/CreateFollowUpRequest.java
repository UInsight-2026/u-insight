// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.dto.request;

import gt.edu.uinsight.followup.entity.FollowUpResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de entrada para {@code POST /api/v1/interventions/{interventionId}/follow-ups}.
 * {@code interventionId} no viaja aquí: se toma del path variable.
 */
public record CreateFollowUpRequest(

        @Schema(description = "Fecha del seguimiento", example = "2026-09-25")
        @NotNull(message = "followUpDate es obligatorio")
        LocalDate followUpDate,

        @Schema(description = "Observación registrada durante el seguimiento",
                example = "El estudiante asistió a la tutoría y mostró mejoría")
        @NotBlank(message = "observation es obligatorio")
        @Size(max = 500, message = "observation no puede superar 500 caracteres")
        String observation,

        @Schema(description = "Resultado observado (opcional)", example = "IMPROVED")
        FollowUpResult result
) {
}
