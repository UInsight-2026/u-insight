package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.dto.response.IntegrationStatusResponse;
import gt.edu.uinsight.system.service.IntegrationStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System - Integration", description = "Estado de integración con otras células")
public class IntegrationStatusController {

    private final IntegrationStatusService service;

    public IntegrationStatusController(IntegrationStatusService service) {
        this.service = service;
    }

    @GetMapping("/integration-status")
    @Operation(
            summary = "Consultar estado de integración",
            description = "Verifica la disponibilidad de módulos de otras células"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado de integración obtenido")
    })
    public ResponseEntity<List<IntegrationStatusResponse>> getIntegrationStatus() {
        return ResponseEntity.ok(service.getIntegrationStatus());
    }
}