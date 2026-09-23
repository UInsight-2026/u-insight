package gt.edu.uinsight.alertrule.service;

import gt.edu.uinsight.alertrule.dto.request.CreateAlertRuleRequest;
import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.entity.AlertRule;
import gt.edu.uinsight.alertrule.repository.AlertRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertRuleService {

    private final AlertRuleRepository repository;

    public AlertRuleService(AlertRuleRepository repository) {
        this.repository = repository;
    }

    public AlertRuleResponse createRule(CreateAlertRuleRequest request) {
        AlertRule rule = new AlertRule();
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setConditionExpression(request.getConditionExpression());
        rule.setSeverity(request.getSeverity());

        AlertRule saved = repository.save(rule);
        return mapToResponse(saved);
    }

    public List<AlertRuleResponse> getRules(Boolean active) {
        List<AlertRule> rules = (active != null) ? repository.findByActive(active) : repository.findAll();
        return rules.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public AlertRuleResponse getRuleById(Long id) {
        AlertRule rule = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla no encontrada con id: " + id));
        return mapToResponse(rule);
    }

    private AlertRuleResponse mapToResponse(AlertRule rule) {
        AlertRuleResponse response = new AlertRuleResponse();
        response.setId(rule.getId());
        response.setName(rule.getName());
        response.setDescription(rule.getDescription());
        response.setConditionExpression(rule.getConditionExpression());
        response.setSeverity(rule.getSeverity());
        response.setActive(rule.getActive());
        response.setCreatedAt(rule.getCreatedAt());
        return response;
    }
}