// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.dto.response;

import gt.edu.uinsight.intervention.entity.InterventionStatus;
import gt.edu.uinsight.intervention.entity.InterventionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de salida para los endpoints de intervención. Nunca se expone la entidad
 * {@code Intervention} directamente.
 */
public record InterventionResponse(

        @Schema(description = "Identificador de la intervención", example = "1")
        Long id,

        @Schema(description = "Identificador de la alerta asociada", example = "10")
        Long alertId,

        @Schema(description = "Tipo de intervención", example = "TUTORING")
        InterventionType type,

        @Schema(description = "Descripción de la intervención", example = "Tutoría de refuerzo en matemática")
        String description,

        @Schema(description = "Persona responsable de ejecutar la intervención", example = "Lic. Ana Pérez")
        String responsible,

        @Schema(description = "Fecha de inicio de la intervención", example = "2026-09-20")
        LocalDate startDate,

        @Schema(description = "Estado actual de la intervención", example = "PLANNED")
        InterventionStatus status,

        @Schema(description = "Fecha y hora de creación del registro", example = "2026-09-14T10:15:30")
        LocalDateTime createdAt
) {
}
