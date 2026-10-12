package gt.edu.uinsight.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.alert.dto.response.RiskEvaluationResponse;
import gt.edu.uinsight.alert.service.RiskEvaluationService;

@RestController
@RequestMapping("/api/v1/risk-evaluation")
public class RiskEvaluationController {

    private final RiskEvaluationService riskEvaluationService;

    public RiskEvaluationController(RiskEvaluationService riskEvaluationService) {
        this.riskEvaluationService = riskEvaluationService;
    }

    @PostMapping("/sections/{sectionId}")
    public ResponseEntity<RiskEvaluationResponse> evaluateSectionRisk(@PathVariable Long sectionId) {
        try {
            RiskEvaluationResponse response = riskEvaluationService.evaluateSectionRisk(sectionId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
