package gt.edu.uinsight.integration.b7;

import gt.edu.uinsight.alertrule.service.AlertRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/integration/b7")
public class B7IntegrationController {

    private final AlertRuleService alertRuleService;

    public B7IntegrationController(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    @GetMapping("/active-rules")
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
