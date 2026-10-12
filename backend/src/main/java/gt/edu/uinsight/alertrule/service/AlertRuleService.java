package gt.edu.uinsight.alertrule.service;

import gt.edu.uinsight.alertrule.dto.request.CreateAlertRuleRequest;
import gt.edu.uinsight.alertrule.dto.request.UpdateStatusRequest;
import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.entity.AlertRule;
import gt.edu.uinsight.alertrule.repository.AlertRuleRepository;
import gt.edu.uinsight.common.exception.ResourceNotFoundException;
import gt.edu.uinsight.integration.c1.C1ConfigurationClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertRuleService {

    private final AlertRuleRepository repository;
    private final C1ConfigurationClient c1Client;

    public AlertRuleService(AlertRuleRepository repository, C1ConfigurationClient c1Client) {
        this.repository = repository;
        this.c1Client = c1Client;
    }

    // --- Mapeador interno de Entidad a DTO Response ---
    private AlertRuleResponse mapToResponse(AlertRule rule) {
        AlertRuleResponse response = new AlertRuleResponse();
        response.setId(rule.getId());
        response.setName(rule.getName());
        response.setDescription(rule.getDescription());
        response.setConditionExpression(rule.getConditionExpression());
        response.setSeverity(rule.getSeverity());
        response.setActive(rule.getActive());
        response.setCreatedAt(rule.getCreatedAt());
        response.setUpdatedAt(rule.getUpdatedAt());
        return response;
    }

    // --- Métodos CRUD retornando AlertRuleResponse ---

    public AlertRuleResponse createRule(CreateAlertRuleRequest request) {
        AlertRule rule = new AlertRule();
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setConditionExpression(request.getConditionExpression());
        rule.setSeverity(request.getSeverity());
        rule.setActive(true);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(repository.save(rule));
    }

    public List<AlertRuleResponse> getAllRules(Boolean active) {
        List<AlertRule> rules;
        if (active != null) {
            rules = repository.findByActive(active);
        } else {
            rules = repository.findAll();
        }
        return rules.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AlertRule> getActiveRules() {
        return repository.findByActiveTrue();
    }

    public AlertRuleResponse getRuleById(Long id) {
        AlertRule rule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert rule not found with id: " + id));
        return mapToResponse(rule);
    }

    public AlertRuleResponse updateRule(Long id, CreateAlertRuleRequest request) {
        AlertRule rule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert rule not found with id: " + id));
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setConditionExpression(request.getConditionExpression());
        rule.setSeverity(request.getSeverity());
        rule.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(repository.save(rule));
    }

    public AlertRuleResponse updateStatus(Long id, UpdateStatusRequest request) {
        AlertRule rule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert rule not found with id: " + id));
        rule.setActive(request.getActive());
        rule.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(repository.save(rule));
    }

    // --- Integración con Célula C1 ---

    public Double getSystemThresholdFromC1() {
        return c1Client.getLowPerformanceThreshold();
    }
}