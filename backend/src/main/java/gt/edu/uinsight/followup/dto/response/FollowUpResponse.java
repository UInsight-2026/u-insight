// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.dto.response;

import gt.edu.uinsight.followup.entity.FollowUpResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO de salida para los endpoints de follow-up. Nunca se expone la entidad
 * {@code FollowUp} directamente.
 */
public record FollowUpResponse(

        @Schema(description = "Identificador del seguimiento", example = "1")
        Long id,

        @Schema(description = "Identificador de la intervención asociada", example = "5")
        Long interventionId,

        @Schema(description = "Fecha del seguimiento", example = "2026-09-25")
        LocalDate followUpDate,

        @Schema(description = "Observación registrada durante el seguimiento",
                example = "El estudiante asistió a la tutoría y mostró mejoría")
        String observation,

        @Schema(description = "Resultado observado (puede ser nulo)", example = "IMPROVED")
        FollowUpResult result
) {
}
