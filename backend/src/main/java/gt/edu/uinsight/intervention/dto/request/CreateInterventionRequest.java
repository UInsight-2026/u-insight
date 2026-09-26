// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.dto.request;

import gt.edu.uinsight.intervention.entity.InterventionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de entrada para {@code POST /api/v1/alerts/{alertId}/interventions}.
 * {@code alertId} no viaja aquí: se toma del path variable.
 */
public record CreateInterventionRequest(

        @Schema(description = "Tipo de intervención", example = "TUTORING")
        @NotNull(message = "type es obligatorio")
        InterventionType type,

        @Schema(description = "Descripción de la intervención", example = "Tutoría de refuerzo en matemática")
        @NotBlank(message = "description es obligatorio")
        @Size(max = 500, message = "description no puede superar 500 caracteres")
        String description,

        @Schema(description = "Persona responsable de ejecutar la intervención", example = "Lic. Ana Pérez")
        @NotBlank(message = "responsible es obligatorio")
        @Size(max = 150, message = "responsible no puede superar 150 caracteres")
        String responsible,

        @Schema(description = "Fecha de inicio de la intervención", example = "2026-09-20")
        @NotNull(message = "startDate es obligatorio")
        LocalDate startDate
) {
}
