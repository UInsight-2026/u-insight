package gt.edu.uinsight.integration.b7;

import gt.edu.uinsight.alertrule.service.AlertRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/integration/b7")
@Tag(name = "B7 - Reglas y evaluación de riesgo",
        description = "Administra reglas activas del motor de riesgo que se usan para evaluar las secciones y generar alertas.")
public class B7IntegrationController {

    private final AlertRuleService alertRuleService;

    public B7IntegrationController(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    @GetMapping("/active-rules")
    @Operation(summary = "Obtener reglas activas para el motor de riesgo",
            description = "Expone únicamente las reglas activas en el formato requerido por el motor de evaluación.")
    @ApiResponse(responseCode = "200", description = "Reglas activas disponibles",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = ActiveRuleIntegrationDto.class))))
    public ResponseEntity<List<ActiveRuleIntegrationDto>> getActiveRulesForRiskEngine() {
        List<ActiveRuleIntegrationDto> activeRules = alertRuleService.getActiveRules().stream()
                .map(rule -> new ActiveRuleIntegrationDto(
                        rule.getId(),
                        rule.getName(),
                        rule.getConditionExpression(),
                        rule.getSeverity()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(activeRules);
    }
}
