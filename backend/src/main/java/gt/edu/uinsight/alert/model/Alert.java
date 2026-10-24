package gt.edu.uinsight.alert.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad de la tabla {@code alert} (celula C3 - Ciclo de vida de las alertas).
 *
 * La misma tabla tambien recibe, por cada evaluacion de riesgo, los campos
 * heredados del motor de B7 (riskLevel, rulesEvaluated, rulesTriggered,
 * alertsGenerated): B7 crea la fila (RiskEvaluationService) y C3 administra su
 * ciclo de vida despues (status, revision, resolucion).
 */
@Entity
@Table(name = "alert")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "alert_type", length = 50)
    private String alertType;

    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AlertStatus status;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

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

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = AlertStatus.NEW;
        }
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
        }
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
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