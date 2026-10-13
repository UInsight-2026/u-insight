package gt.edu.uinsight.alert.controller;

import gt.edu.uinsight.alert.dto.request.UpdateAlertStatusRequest;
import gt.edu.uinsight.alert.dto.response.AlertResponse;
import gt.edu.uinsight.alert.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Celula C3 - Ciclo de vida de las alertas.
 * Administra una alerta despues de ser generada por la celula B7.
 */
@Tag(name = "Alertas", description = "Ciclo de vida de las alertas generadas por el motor de riesgo")
@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @Operation(summary = "Listar todas las alertas")
    @GetMapping
    public ResponseEntity<List<AlertResponse>> findAll() {
        return ResponseEntity.ok(alertService.findAll());
    }

    @Operation(summary = "Listar alertas activas",
            description = "Alertas que no están en estado RESOLVED ni DISMISSED.")
    @GetMapping("/active")
    public ResponseEntity<List<AlertResponse>> findActive() {
        return ResponseEntity.ok(alertService.findActive());
    }

    @Operation(summary = "Obtener una alerta por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alerta encontrada"),
            @ApiResponse(responseCode = "404", description = "La alerta indicada no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> findById(
            @Parameter(description = "Id de la alerta", example = "1012") @PathVariable Long id) {
        return ResponseEntity.ok(alertService.findById(id));
    }

    @Operation(summary = "Cambiar el estado de una alerta",
            description = "Transiciones válidas: NEW→UNDER_REVIEW→IN_PROGRESS→RESOLVED, "
                    + "y NEW/UNDER_REVIEW/IN_PROGRESS→DISMISSED. No se permiten saltos ni retrocesos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "La alerta indicada no existe"),
            @ApiResponse(responseCode = "409", description = "Transición de estado no permitida")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<AlertResponse> updateStatus(
            @Parameter(description = "Id de la alerta", example = "1012") @PathVariable Long id,
            @Valid @RequestBody UpdateAlertStatusRequest request) {
        return ResponseEntity.ok(alertService.updateStatus(id, request));
    }
}
