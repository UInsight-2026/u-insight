package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.dto.response.ReadinessResponse;
import gt.edu.uinsight.system.service.ReadinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System - Readiness", description = "Endpoint de verificacion de disponibilidad de la aplicacion")
public class ReadinessController {

    private final ReadinessService readinessService;

    public ReadinessController(ReadinessService readinessService) {
        this.readinessService = readinessService;
    }

    @GetMapping("/readiness")
    @Operation(
            summary = "Verificar disponibilidad de la aplicacion",
            description = "Verifica la disponibilidad de la base de datos, las configuraciones "
                    + "y los servicios criticos. Devuelve 200 si todos los componentes estan disponibles "
                    + "y 503 si alguno no lo esta."
    )
   @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "La aplicacion esta lista",
                content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = """
                                        {
                                          "ready": true,
                                          "database": "UP",
                                          "configuration": "UP",
                                          "criticalServices": "UP"
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "503",
                description = "La aplicacion no esta lista",
                content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = """
                                        {
                                          "ready": false,
                                          "database": "DOWN",
                                          "configuration": "UP",
                                          "criticalServices": "UP"
                                        }
                                        """
                        )
                )
        )
})
    public ResponseEntity<ReadinessResponse> readiness() {

        ReadinessResponse response = readinessService.checkReadiness();

        if (response.ready()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}