package gt.edu.uinsight.alert.service;

import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.model.AlertStatus;
import gt.edu.uinsight.alert.repository.AlertRepository;
import gt.edu.uinsight.alert.dto.response.RiskEvaluationResponse;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;

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

    @Transactional
    public RiskEvaluationResponse evaluateSectionRisk(Long sectionId) {

        SectionSummary summary = summaryService.getSummary(sectionId);

        if (summary == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "No hay indicadores disponibles para la sección " + sectionId);
        }

        RiskResult result = riskEngine.evaluate(summary);
        if (result.getRulesEvaluated() == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "No hay reglas activas evaluables con los datos disponibles para la sección " + sectionId);
        }
        if (sectionId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("El identificador de sección excede el rango soportado.");
        }

        int alertsGenerated = 0;
        for (String ruleName : result.getActivatedRules()) {
            String title = buildTitle(ruleName);
            if (alertRepository.existsBySectionIdAndAlertTypeAndTitleAndStatusNotIn(
                    sectionId.intValue(),
                    "RISK_EVALUATION",
                    title,
                    EnumSet.of(AlertStatus.RESOLVED, AlertStatus.DISMISSED))) {
                continue;
            }

            Alert alert = new Alert();
            alert.setSectionId(sectionId.intValue());
            alert.setRiskLevel(result.getRiskLevel());
            alert.setRulesEvaluated(result.getRulesEvaluated());
            alert.setRulesTriggered(result.getRulesTriggered());
            alert.setAlertsGenerated(1);
            alert.setAlertType("RISK_EVALUATION");
            alert.setSeverity(result.getRiskLevel());
            alert.setTitle(title);
            alert.setDescription("Regla activada: " + ruleName);
            alertRepository.save(alert);
            alertsGenerated++;
        }

        result.setAlertsGenerated(alertsGenerated);

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

    private String buildTitle(String ruleName) {
        return "Riesgo detectado: " + ruleName;
    }
}
