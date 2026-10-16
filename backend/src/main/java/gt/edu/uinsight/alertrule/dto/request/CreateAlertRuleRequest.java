package gt.edu.uinsight.alertrule.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para crear o actualizar una regla de riesgo")
public class CreateAlertRuleRequest {
    @Schema(description = "Nombre único de la regla", example = "Bajo rendimiento")
    private String name;
    @Schema(description = "Descripción funcional de la regla", example = "Detecta promedios inferiores al umbral establecido")
    private String description;
    @Schema(
            description = "Expresión del motor. Variables numéricas: average, median, dispersion, percentile90, "
                    + "studentsAtRisk, studentsAtRiskPercentage. La tendencia acepta comparación == con clasificación.",
            example = "average < 60 && trend == NEGATIVE")
    private String conditionExpression;
    @Schema(description = "Severidad de la regla", allowableValues = {"LOW", "MEDIUM", "HIGH"}, example = "HIGH")
    private String severity;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getConditionExpression() { return conditionExpression; }
    public void setConditionExpression(String conditionExpression) { this.conditionExpression = conditionExpression; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}