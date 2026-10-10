package gt.edu.uinsight.alert.controller;

import gt.edu.uinsight.alert.dto.AlertResponse;
import gt.edu.uinsight.alert.dto.CreateAlertRequest;
import gt.edu.uinsight.alert.dto.UpdateAlertStatusRequest;
import gt.edu.uinsight.alert.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alerts")
@Tag(name = "Alerts", description = "API para el ciclo de vida de alertas (Célula C3)")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // --- ENDPOINT TÉCNICO DE SALUD (SEMANA 5) ---
    @GetMapping("/health")
    @Operation(summary = "Verificar estado del módulo", description = "Endpoint técnico para validar que el controlador de Alertas está en funcionamiento")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("module", "Alerts - Célula C3");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Crear nueva alerta", description = "Endpoint de integración para que otras células generen alertas. Incluye validación Jakarta.")
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody CreateAlertRequest request) {
        return ResponseEntity.ok(alertService.createAlert(request));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las alertas", description = "Devuelve el historial completo de alertas registradas en el sistema")
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener alertas activas", description = "Filtra y devuelve únicamente las alertas que requieren atención (excluye estados RESOLVED y DISMISSED)")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts() {
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una alerta", description = "Busca una alerta específica por su ID único")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de alerta", description = "Cambia el estado aplicando reglas estrictas (RN01, RN03, RN05). Errores atrapados por GlobalExceptionHandler.")
    public ResponseEntity<AlertResponse> updateAlertStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAlertStatusRequest request) {
        return ResponseEntity.ok(alertService.updateAlertStatus(id, request));
    }
}