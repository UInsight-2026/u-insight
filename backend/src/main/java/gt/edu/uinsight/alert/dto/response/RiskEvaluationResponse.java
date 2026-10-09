package gt.edu.uinsight.alert.dto.response;

import java.util.List;

public class RiskEvaluationResponse {

    private Integer sectionId;
    private String riskLevel;
    private double score;

    private int rulesEvaluated;
    private int rulesTriggered;
    private int alertsGenerated;
    private List<String> activatedRules;
    
    public RiskEvaluationResponse() {
    }

    public RiskEvaluationResponse(
            Integer sectionId,
            String riskLevel,
            Double score,
            Integer rulesEvaluated,
            Integer rulesTriggered,
            Integer alertsGenerated,
            List<String> activatedRules
    ) {
        this.sectionId = sectionId;
        this.riskLevel = riskLevel;
        this.score = score;
        this.rulesEvaluated = rulesEvaluated;
        this.rulesTriggered = rulesTriggered;
        this.alertsGenerated = alertsGenerated;
        this.activatedRules = activatedRules;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getRulesEvaluated() {
        return rulesEvaluated;
    }

    public void setRulesEvaluated(Integer rulesEvaluated) {
        this.rulesEvaluated = rulesEvaluated;
    }

    public Integer getRulesTriggered() {
        return rulesTriggered;
    }

    public void setRulesTriggered(Integer rulesTriggered) {
        this.rulesTriggered = rulesTriggered;
    }

    public Integer getAlertsGenerated() {
        return alertsGenerated;
    }

    public void setAlertsGenerated(Integer alertsGenerated) {
        this.alertsGenerated = alertsGenerated;
    }

    public List<String> getActivatedRules() {
        return activatedRules;
    }

    public void setActivatedRules(List<String> activatedRules) {
        this.activatedRules = activatedRules;
    }
}