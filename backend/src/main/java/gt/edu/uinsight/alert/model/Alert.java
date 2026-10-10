package gt.edu.uinsight.alert.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alert")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "rules_evaluated")
    private Integer rulesEvaluated;

    @Column(name = "rules_triggered")
    private Integer rulesTriggered;

    @Column(name = "alerts_generated")
    private Integer alertsGenerated;

    public Alert() {
    }

    public Alert(
            Integer sectionId,
            String riskLevel,
            Integer rulesEvaluated,
            Integer rulesTriggered,
            Integer alertsGenerated
    ) {
        this.sectionId = sectionId;
        this.riskLevel = riskLevel;
        this.rulesEvaluated = rulesEvaluated;
        this.rulesTriggered = rulesTriggered;
        this.alertsGenerated = alertsGenerated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}