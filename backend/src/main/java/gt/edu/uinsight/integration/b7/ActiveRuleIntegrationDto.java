package gt.edu.uinsight.integration.b7;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de una regla activa para el motor de riesgo")
public class ActiveRuleIntegrationDto {
    @Schema(description = "Identificador de la regla", example = "4")
    private Long id;
    @Schema(description = "Nombre de la regla", example = "Bajo rendimiento")
    private String name;
    @Schema(description = "Expresión condicional evaluada por el motor", example = "average < threshold")
    private String conditionExpression;
    @Schema(description = "Severidad de la regla", example = "HIGH")
    private String severity;

    public ActiveRuleIntegrationDto(Long id, String name, String conditionExpression, String severity) {
        this.id = id;
        this.name = name;
        this.conditionExpression = conditionExpression;
        this.severity = severity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getConditionExpression() { return conditionExpression; }
    public void setConditionExpression(String conditionExpression) { this.conditionExpression = conditionExpression; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}