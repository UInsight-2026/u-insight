package gt.edu.uinsight.alertrule.controller;

import gt.edu.uinsight.alertrule.dto.request.CreateAlertRuleRequest;
import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
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
    }
}
