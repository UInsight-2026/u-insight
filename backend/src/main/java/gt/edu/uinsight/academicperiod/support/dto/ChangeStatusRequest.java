package gt.edu.uinsight.academicperiod.support.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de PATCH /{id}/status para periodos y cursos. El valor se valida contra el
 * enum correspondiente en la capa de servicio: un estado no reconocido responde 400.
 */
public record ChangeStatusRequest(
        @Schema(description = "Nuevo estado", example = "ACTIVE")
        @NotBlank String status
) {
}
