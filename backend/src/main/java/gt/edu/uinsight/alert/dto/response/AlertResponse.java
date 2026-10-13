package gt.edu.uinsight.alert.dto.response;

import gt.edu.uinsight.alert.model.AlertStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO de salida de la Celula C3 (ciclo de vida de alertas). Nunca se expone
 * la entidad {@code Alert} directamente.
 */
public record AlertResponse(

        @Schema(description = "Identificador de la alerta", example = "1012")
        Long id,

        @Schema(description = "Identificador de la sección a la que pertenece la alerta", example = "10")
        Integer sectionId,

        @Schema(description = "Tipo de alerta", example = "RISK_EVALUATION")
        String alertType,

        @Schema(description = "Severidad de la alerta", example = "HIGH")
        String severity,

        @Schema(description = "Título descriptivo de la alerta")
        String title,

        @Schema(description = "Descripción de la alerta (reglas activadas, detalle)")
        String description,

        @Schema(description = "Estado actual en el ciclo de vida", example = "NEW")
        AlertStatus status,

        @Schema(description = "Fecha y hora en la que se generó la alerta")
        LocalDateTime generatedAt,

        @Schema(description = "Fecha y hora en la que se resolvió o descartó la alerta (null si sigue abierta)")
        LocalDateTime resolvedAt
) {
}
