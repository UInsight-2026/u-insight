package gt.edu.uinsight.alert.dto.response;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado de evaluar las reglas de riesgo para una sección")
public class RiskEvaluationResponse {

    @Schema(description = "Identificador de la sección evaluada", example = "10")
    private Long sectionId;
    @Schema(description = "Nivel de riesgo calculado", example = "HIGH")
    private String riskLevel;
    @Schema(description = "Puntaje de riesgo calculado", example = "0.82")
    private double score;

    @Schema(description = "Cantidad de reglas activas evaluadas", example = "5")
    private int rulesEvaluated;
    @Schema(description = "Cantidad de reglas que se activaron", example = "2")
    private int rulesTriggered;
    @Schema(description = "Cantidad de alertas generadas", example = "1")
    private int alertsGenerated;
    @Schema(description = "Nombres de las reglas que se activaron", example = "[\"Bajo rendimiento\"]")
    private List<String> activatedRules;
    
    public RiskEvaluationResponse() {
    }

    public RiskEvaluationResponse(
            Long sectionId,
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

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
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