package gt.edu.uinsight.alert.dto.request;

import gt.edu.uinsight.alert.model.AlertStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para {@code PATCH /api/v1/alerts/{id}/status}.
 */
public record UpdateAlertStatusRequest(

        @Schema(description = "Nuevo estado de la alerta", example = "UNDER_REVIEW")
        @NotNull(message = "status es obligatorio")
        AlertStatus status
) {
}
