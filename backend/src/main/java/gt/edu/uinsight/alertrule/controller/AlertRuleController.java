package gt.edu.uinsight.alertrule.controller;

import gt.edu.uinsight.alertrule.dto.request.CreateAlertRuleRequest;
import gt.edu.uinsight.alertrule.dto.request.UpdateStatusRequest;
import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alert-rules")
@Tag(name = "B7 - Reglas y evaluación de riesgo",
        description = "Administra reglas activas del motor de riesgo que se usan para evaluar las secciones y generar alertas.")
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    public AlertRuleController(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    // HU-C2-01: Crear Regla de Alerta
    @PostMapping
    @Operation(summary = "Crear una regla de riesgo",
            description = "Crea una regla activa. La severidad permitida es LOW, MEDIUM o HIGH.")
    @ApiResponse(responseCode = "201", description = "Regla creada",
            content = @Content(schema = @Schema(implementation = AlertRuleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Nombre duplicado, severidad no permitida o expresión condicional vacía")
    public ResponseEntity<AlertRuleResponse> createRule(@Valid @RequestBody CreateAlertRuleRequest request) {
        AlertRuleResponse response = alertRuleService.createRule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // HU-C2-02: Consultar Reglas Activas / Todas
    @GetMapping
    @Operation(summary = "Listar reglas de riesgo",
            description = "Devuelve todas las reglas o filtra por estado activo cuando se especifica active.")
    @ApiResponse(responseCode = "200", description = "Lista de reglas",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = AlertRuleResponse.class))))
    public ResponseEntity<List<AlertRuleResponse>> getAllRules(
            @Parameter(description = "Filtra por estado de activación", example = "true")
            @RequestParam(value = "active", required = false) Boolean active) {
        List<AlertRuleResponse> list = alertRuleService.getAllRules(active);
        return ResponseEntity.ok(list);
    }

    // Consultar detalle de una regla por ID
    @GetMapping("/{id}")
    @Operation(summary = "Consultar una regla de riesgo",
            description = "Obtiene los datos y el estado de una regla por su identificador.")
    @ApiResponse(responseCode = "200", description = "Regla encontrada",
            content = @Content(schema = @Schema(implementation = AlertRuleResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe una regla con ese identificador")
    public ResponseEntity<AlertRuleResponse> getRuleById(
            @Parameter(description = "Identificador de la regla", example = "4")
            @PathVariable Long id) {
        AlertRuleResponse response = alertRuleService.getRuleById(id);
        return ResponseEntity.ok(response);
    }

    // HU-C2-03: Modificar Regla de Alerta
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una regla de riesgo",
            description = "Reemplaza los datos editables de la regla identificada; conserva su estado de activación.")
    @ApiResponse(responseCode = "200", description = "Regla actualizada",
            content = @Content(schema = @Schema(implementation = AlertRuleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Nombre duplicado o severidad no permitida")
    @ApiResponse(responseCode = "404", description = "No existe una regla con ese identificador")
    public ResponseEntity<AlertRuleResponse> updateRule(
            @Parameter(description = "Identificador de la regla", example = "4") @PathVariable Long id,
            @Valid @RequestBody CreateAlertRuleRequest request) {
        AlertRuleResponse response = alertRuleService.updateRule(id, request);
        return ResponseEntity.ok(response);
    }

    // HU-C2-04: Activar/Desactivar Regla de Alerta (Cambio de estado)
    @PatchMapping("/{id}/status")
    @Operation(summary = "Activar o desactivar una regla de riesgo",
            description = "Cambia únicamente el atributo active de la regla.")
    @ApiResponse(responseCode = "200", description = "Estado actualizado",
            content = @Content(schema = @Schema(implementation = AlertRuleResponse.class)))
    @ApiResponse(responseCode = "400", description = "El atributo active es obligatorio")
    @ApiResponse(responseCode = "404", description = "No existe una regla con ese identificador")
    public ResponseEntity<AlertRuleResponse> updateStatus(
            @Parameter(description = "Identificador de la regla", example = "4") @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        AlertRuleResponse response = alertRuleService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
