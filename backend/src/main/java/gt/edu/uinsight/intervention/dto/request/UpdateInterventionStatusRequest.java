// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.dto.request;

import gt.edu.uinsight.intervention.entity.InterventionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para {@code PATCH /api/v1/interventions/{id}/status}.
 */
public record UpdateInterventionStatusRequest(

        @Schema(description = "Nuevo estado de la intervención", example = "IN_PROGRESS")
        @NotNull(message = "status es obligatorio")
        InterventionStatus status
) {
}
