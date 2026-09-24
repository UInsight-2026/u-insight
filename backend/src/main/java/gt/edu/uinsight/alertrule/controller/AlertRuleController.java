package gt.edu.uinsight.alertrule.controller;

import gt.edu.uinsight.alertrule.dto.request.CreateAlertRuleRequest;
feature/semana-2-c2
import gt.edu.uinsight.alertrule.dto.request.UpdateStatusRequest;
import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
import jakarta.validation.Valid;

import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
main
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alert-rules")
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    public AlertRuleController(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

feature/semana-2-c2
    // HU-C2-01: Crear Regla de Alerta
    @PostMapping
    public ResponseEntity<AlertRuleResponse> createRule(@Valid @RequestBody CreateAlertRuleRequest request) {
        AlertRuleResponse response = alertRuleService.createRule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // HU-C2-02: Consultar Reglas Activas / Todas
    @GetMapping
    public ResponseEntity<List<AlertRuleResponse>> getAllRules(
            @RequestParam(value = "active", required = false) Boolean active) {
        List<AlertRuleResponse> list = alertRuleService.getAllRules(active);
        return ResponseEntity.ok(list);
    }

    // Consultar detalle de una regla por ID
    @GetMapping("/{id}")
    public ResponseEntity<AlertRuleResponse> getRuleById(@PathVariable Long id) {
        AlertRuleResponse response = alertRuleService.getRuleById(id);
        return ResponseEntity.ok(response);
    }

    // HU-C2-03: Modificar Regla de Alerta
    @PutMapping("/{id}")
    public ResponseEntity<AlertRuleResponse> updateRule(
            @PathVariable Long id,
            @Valid @RequestBody CreateAlertRuleRequest request) {
        AlertRuleResponse response = alertRuleService.updateRule(id, request);
        return ResponseEntity.ok(response);
    }

    // HU-C2-04: Activar/Desactivar Regla de Alerta (Cambio de estado)
    @PatchMapping("/{id}/status")
    public ResponseEntity<AlertRuleResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        AlertRuleResponse response = alertRuleService.updateStatus(id, request);
        return ResponseEntity.ok(response);

    @PostMapping
    public ResponseEntity<AlertRuleResponse> createRule(@RequestBody CreateAlertRuleRequest request) {
        return new ResponseEntity<>(alertRuleService.createRule(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlertRuleResponse>> getRules(@RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(alertRuleService.getRules(active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertRuleResponse> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(alertRuleService.getRuleById(id));
 main
    }
}
