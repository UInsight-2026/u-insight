package gt.edu.uinsight.integration.b7;

public class ActiveRuleIntegrationDto {
    private Long id;
    private String name;
    private String conditionExpression;
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