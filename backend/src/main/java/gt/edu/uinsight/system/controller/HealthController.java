package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.dto.response.HealthResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.service.DatabaseHealthIndicator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System - Health", description = "Endpoints tecnicos de verificacion de estado")
public class HealthController {

    private final DatabaseHealthIndicator databaseHealthIndicator;

    public HealthController(DatabaseHealthIndicator databaseHealthIndicator) {
        this.databaseHealthIndicator = databaseHealthIndicator;
    }

    @GetMapping("/health")
    @Operation(
            summary = "Verificar que la aplicacion esta en funcionamiento",
            description = "Devuelve el estado de la aplicacion y el de su conexion con la base de datos. "
                    + "La aplicacion responde UP mientras el proceso este vivo; el estado de la base de "
                    + "datos es informativo y no condiciona el codigo HTTP."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La aplicacion esta en funcionamiento")
    })
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse(
                CheckStatus.UP,
                databaseHealthIndicator.check()
        ));
    }
}
