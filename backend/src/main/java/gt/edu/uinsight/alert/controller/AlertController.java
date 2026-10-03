package gt.edu.uinsight.alert.controller;

import gt.edu.uinsight.alert.dto.AlertResponse;
import gt.edu.uinsight.alert.dto.CreateAlertRequest;
import gt.edu.uinsight.alert.dto.UpdateAlertStatusRequest;
import gt.edu.uinsight.alert.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@Tag(name = "Alerts", description = "API para el ciclo de vida de alertas (Célula C3)")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    @Operation(summary = "Crear nueva alerta", description = "Endpoint de integración para que otras células generen alertas")
    public ResponseEntity<AlertResponse> createAlert(@RequestBody CreateAlertRequest request) {
        return ResponseEntity.ok(alertService.createAlert(request));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las alertas")
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener alertas activas", description = "Filtra las alertas que no estén en estado RESOLVED o DISMISSED")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts() {
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una alerta por ID")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estado de alerta", description = "Actualiza el estado aplicando reglas de negocio estrictas")
    public ResponseEntity<?> updateAlertStatus(
            @PathVariable Long id,
            @RequestBody UpdateAlertStatusRequest request) {
        try {
            return ResponseEntity.ok(alertService.updateAlertStatus(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}