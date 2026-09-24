package gt.edu.uinsight.alertrule.service;

import gt.edu.uinsight.alertrule.dto.request.CreateAlertRuleRequest;
feature/semana-2-c2
import gt.edu.uinsight.alertrule.dto.request.UpdateStatusRequest;
main
import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.entity.AlertRule;
import gt.edu.uinsight.alertrule.repository.AlertRuleRepository;
import org.springframework.stereotype.Service;

 feature/semana-2-c2
import java.util.Arrays;
 main
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertRuleService {

    private final AlertRuleRepository repository;
 feature/semana-2-c2
    private static final List<String> ALLOWED_SEVERITIES = Arrays.asList("LOW", "MEDIUM", "HIGH");
 main

    public AlertRuleService(AlertRuleRepository repository) {
        this.repository = repository;
    }

 feature/semana-2-c2
    // HU-C2-01: Crear Regla
    public AlertRuleResponse createRule(CreateAlertRuleRequest request) {
        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Ya existe una regla registrada con el nombre: " + request.getName());
        }

        validateSeverity(request.getSeverity());

        if (request.getConditionExpression() == null || request.getConditionExpression().trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión condicional no puede estar vacía.");
        }


    public AlertRuleResponse createRule(CreateAlertRuleRequest request) {
 main
        AlertRule rule = new AlertRule();
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setConditionExpression(request.getConditionExpression());
 feature/semana-2-c2
        rule.setSeverity(request.getSeverity().toUpperCase());
        rule.setActive(true);

        rule.setSeverity(request.getSeverity());
 main

        AlertRule saved = repository.save(rule);
        return mapToResponse(saved);
    }

 feature/semana-2-c2
    // HU-C2-02: Consultar Reglas (filtro por activas)
    public List<AlertRuleResponse> getAllRules(Boolean activeOnly) {
        List<AlertRule> rules;
        if (Boolean.TRUE.equals(activeOnly)) {
            rules = repository.findByActive(true);
        } else {
            rules = repository.findAll();
        }

    public List<AlertRuleResponse> getRules(Boolean active) {
        List<AlertRule> rules = (active != null) ? repository.findByActive(active) : repository.findAll();
 main
        return rules.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public AlertRuleResponse getRuleById(Long id) {
        AlertRule rule = repository.findById(id)
 feature/semana-2-c2
                .orElseThrow(() -> new RuntimeException("Alert rule not found with id: " + id));
        return mapToResponse(rule);
    }

    // HU-C2-03: Modificar Regla
    public AlertRuleResponse updateRule(Long id, CreateAlertRuleRequest request) {
        AlertRule rule = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert rule not found"));

        if (repository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new IllegalArgumentException("Nombre de regla en uso por otro registro.");
        }

        validateSeverity(request.getSeverity());

        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setConditionExpression(request.getConditionExpression());
        rule.setSeverity(request.getSeverity().toUpperCase());

        AlertRule updated = repository.save(rule);
        return mapToResponse(updated);
    }

    // HU-C2-04: Activar / Desactivar (PATCH)
    public AlertRuleResponse updateStatus(Long id, UpdateStatusRequest request) {
        AlertRule rule = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert rule not found"));

        rule.setActive(request.getActive());
        AlertRule updated = repository.save(rule);
        return mapToResponse(updated);
    }

    private void validateSeverity(String severity) {
        if (severity == null || !ALLOWED_SEVERITIES.contains(severity.toUpperCase())) {
            throw new IllegalArgumentException("Severidad inválida. Debe ser LOW, MEDIUM o HIGH.");
        }
    }

    private AlertRuleResponse mapToResponse(AlertRule rule) {
        AlertRuleResponse res = new AlertRuleResponse();
        res.setId(rule.getId());
        res.setName(rule.getName());
        res.setDescription(rule.getDescription());
        res.setConditionExpression(rule.getConditionExpression());
        res.setSeverity(rule.getSeverity());
        res.setActive(rule.getActive());
        res.setCreatedAt(rule.getCreatedAt());
        res.setUpdatedAt(rule.getUpdatedAt());
        return res;

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
 main
    }
}