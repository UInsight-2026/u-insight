package gt.edu.uinsight.alert.engine;

import java.util.List;

public class RiskResult {

    private String riskLevel;
    private Double score;
    private Integer rulesEvaluated;
    private Integer rulesTriggered;
    private Integer alertsGenerated;
    private List<String> activatedRules;

    public RiskResult() {
    }

    public RiskResult(
            String riskLevel,
            Double score,
            Integer rulesEvaluated,
            Integer rulesTriggered,
            Integer alertsGenerated,
            List<String> activatedRules
    ) {
        this.riskLevel = riskLevel;
        this.score = score;
        this.rulesEvaluated = rulesEvaluated;
        this.rulesTriggered = rulesTriggered;
        this.alertsGenerated = alertsGenerated;
        this.activatedRules = activatedRules;
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