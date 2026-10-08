package gt.edu.uinsight.alertrule.controller;
import gt.edu.uinsight.alertrule.dto.request.CreateAlertRuleRequest;
import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
import gt.edu.uinsight.intervention.service.AlertRuleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alert-rules")
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    public AlertRuleController(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    @PostMapping
    public ResponseEntity<AlertRuleResponse> createRule(
            @RequestBody CreateAlertRuleRequest request) {

        AlertRuleResponse response = alertRuleService.createRule(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AlertRuleResponse>> getRules() {
        return ResponseEntity.ok(alertRuleService.getRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertRuleResponse> getRuleById(
            @PathVariable Long id) {

        return ResponseEntity.ok(alertRuleService.getRuleById(id));
    }

    @ExceptionHandler(AlertRuleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRuleNotFound(
            AlertRuleNotFoundException ex) {

        Map<String, Object> error = Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "NOT_FOUND",
                "message", ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}