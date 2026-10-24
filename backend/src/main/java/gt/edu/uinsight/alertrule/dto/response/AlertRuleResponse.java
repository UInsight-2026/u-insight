package gt.edu.uinsight.alertrule.dto.response;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Regla de riesgo y estado persistido")
public class AlertRuleResponse {
    @Schema(description = "Identificador de la regla", example = "4")
    private Long id;
    @Schema(description = "Nombre único de la regla", example = "Bajo rendimiento")
    private String name;
    @Schema(description = "Descripción funcional de la regla")
    private String description;
    @Schema(description = "Expresión condicional usada por el motor de riesgo", example = "average < threshold")
    private String conditionExpression;
    @Schema(description = "Severidad (LOW, MEDIUM o HIGH)", example = "HIGH")
    private String severity;
    @Schema(description = "Indica si el motor utiliza la regla", example = "true")
    private Boolean active;
    @Schema(description = "Fecha y hora de creación")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha y hora de última modificación")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getConditionExpression() { return conditionExpression; }
    public void setConditionExpression(String conditionExpression) { this.conditionExpression = conditionExpression; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}