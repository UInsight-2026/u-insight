package gt.edu.uinsight.system.dto.request;

import gt.edu.uinsight.system.entity.CheckStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO propio de la operacion de cambio de estado (seccion 9.3: un DTO por
 * operacion, nunca uno generico).
 */
public record UpdateCheckStatusRequest(

        @NotNull(message = "status is required")
        CheckStatus status
) {
}
