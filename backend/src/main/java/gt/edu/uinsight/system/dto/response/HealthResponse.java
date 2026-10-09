package gt.edu.uinsight.system.dto.response;

import gt.edu.uinsight.system.entity.CheckStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado operativo de la aplicacion")
public record HealthResponse(

        @Schema(description = "Estado de la aplicacion", example = "UP")
        CheckStatus status,

        @Schema(description = "Estado de la conexion con la base de datos", example = "UP")
        CheckStatus database
) {
}
