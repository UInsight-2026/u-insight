package gt.edu.uinsight.alertrule.dto.request;

public class CreateAlertRuleRequest {
    private String name;
    private String description;
    private String conditionExpression;
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