package gt.edu.uinsight.system.dto.response;

import gt.edu.uinsight.system.entity.CheckStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado de disponibilidad de los componentes críticos de la aplicación")
public record ReadinessResponse(

        @Schema(description = "Indica si la aplicación está lista para recibir solicitudes", example = "true")
        boolean ready,

        @Schema(description = "Estado de la base de datos", example = "UP")
        CheckStatus database,

        @Schema(description = "Estado de las configuraciones", example = "UP")
        CheckStatus configuration,

        @Schema(description = "Estado de los servicios críticos", example = "UP")
        CheckStatus criticalServices
) {
}