package gt.edu.uinsight.alert.service;

import org.springframework.stereotype.Service;

import gt.edu.uinsight.alert.dto.response.RiskEvaluationResponse;
import gt.edu.uinsight.alert.dto.section.SectionIndicatorsDto;
import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.repository.AlertRepository;

@Service
public class RiskEvaluationService {

    private final RiskEngine riskEngine;
    private final AlertRepository alertRepository;

    public RiskEvaluationService(RiskEngine riskEngine, AlertRepository alertRepository) {
        this.riskEngine = riskEngine;
        this.alertRepository = alertRepository;
    }

    public RiskEvaluationResponse evaluateSectionRisk(Integer sectionId, SectionIndicatorsDto indicatorsDto) {

        RiskResult result = riskEngine.evaluate(indicatorsDto);

        Alert alert = new Alert(
                sectionId,
                result.getRiskLevel(),
                result.getRulesEvaluated(),
                result.getRulesTriggered(),
                result.getAlertsGenerated()
        );

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
}
