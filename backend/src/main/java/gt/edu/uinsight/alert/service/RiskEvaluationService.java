package gt.edu.uinsight.alert.service;

import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.repository.AlertRepository;
import gt.edu.uinsight.alert.dto.response.RiskEvaluationResponse;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import org.springframework.stereotype.Service;

@Service
public class RiskEvaluationService {

    private final RiskEngine riskEngine;
    private final SummaryService summaryService;
    private final AlertRepository alertRepository;

    public RiskEvaluationService(
            RiskEngine riskEngine,
            SummaryService summaryService,
            AlertRepository alertRepository
    ) {
        this.riskEngine = riskEngine;
        this.summaryService = summaryService;
        this.alertRepository = alertRepository;
    }

    public RiskEvaluationResponse evaluateSectionRisk(Long sectionId) {

        SectionSummary summary = summaryService.getSummary(sectionId);

        if (summary == null) {
            throw new RuntimeException("No hay indicadores disponibles para la sección " + sectionId);
        }

        RiskResult result = riskEngine.evaluate(summary);

        Alert alert = new Alert();
        alert.setSectionId(sectionId.intValue());
        alert.setRiskLevel(result.getRiskLevel());
        alert.setRulesEvaluated(result.getRulesEvaluated());
        alert.setRulesTriggered(result.getRulesTriggered());
        alert.setAlertsGenerated(result.getAlertsGenerated());
        alert.setAlertType("RISK_EVALUATION");
        alert.setSeverity(result.getRiskLevel());
        alert.setTitle(buildTitle(result, sectionId));
        if (!result.getActivatedRules().isEmpty()) {
            alert.setDescription("Reglas activadas: " + String.join(", ", result.getActivatedRules()));
        }

        alertRepository.save(alert);

        return new RiskEvaluationResponse(
                sectionId,
                result.getRiskLevel(),
                result.getScore(),
                result.getRulesEvaluated(),
                result.getRulesTriggered(),
                result.getAlertsGenerated(),
                result.getActivatedRules()
        );
    }

    private String buildTitle(RiskResult result, Long sectionId) {
        return "Riesgo " + result.getRiskLevel() + " detectado en la sección " + sectionId;
    }
}
