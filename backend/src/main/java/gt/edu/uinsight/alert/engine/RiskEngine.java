package gt.edu.uinsight.alert.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gt.edu.uinsight.alertrule.entity.AlertRule;
import gt.edu.uinsight.alertrule.service.AlertRuleService;

import gt.edu.uinsight.analytics.summary.entity.SectionSummary;

public class RiskEngine {

    private final AlertRuleService alertRuleService;

    public RiskEngine(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    public RiskResult evaluate(SectionSummary summary) {

        List<AlertRule> rules = alertRuleService.getActiveRules();
        List<String> activatedRules = new ArrayList<>();

        int rulesEvaluated = rules.size();
        int rulesTriggered = 0;
        double score = 0;

        Map<String, Object> ct = convert(summary.getCentralTendencyData());
        Map<String, Object> pos = convert(summary.getPositionData());
        Map<String, Object> disp = convert(summary.getDispersionData());
        Map<String, Object> trend = convert(summary.getTrendData());
        Map<String, Object> comp = convert(summary.getStudentComparisonData());

        //Extraer los valores reales
        Double media = getDouble(ct, "mean");
        Double mediana = getDouble(ct, "median");

        Double desviacion = getDouble(disp, "stdDev");

        String tendencia = getString(trend, "value");

        Double percentil90 = getDouble(pos, "percentile90");

        Integer studentsAtRisk = comp != null ? getInt(comp, "studentsAtRisk") : null;

        //Evaluar las reglas
        for (AlertRule rule : rules) {

            if (!Boolean.TRUE.equals(rule.getActive())) continue;

            boolean triggered = evaluateRule(
                    rule.getConditionExpression(),
                    media, mediana, desviacion,
                    tendencia, percentil90, studentsAtRisk
            );

            if (triggered) {
                activatedRules.add(rule.getName());
                rulesTriggered++;
                score += getSeverityWeight(rule.getSeverity());
            }
        }

        String riskLevel = calculateRiskLevel(score);

        return new RiskResult(
                riskLevel,
                score,
                rulesEvaluated,
                rulesTriggered,
                rulesTriggered,
                activatedRules
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convert(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        return null;
    }

    //extraer valores
    private Double getDouble(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        return null;
    }

    private Integer getInt(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        return null;
    }

    private String getString(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    //Evaluacion de las reglas
    private boolean evaluateRule(String expr,
                                 Double media,
                                 Double mediana,
                                 Double desviacion,
                                 String tendencia,
                                 Double percentil90,
                                 Integer studentsAtRisk) {

        String[] parts = expr.split("&&");

        for (String part : parts) {
            part = part.trim();

            if (!evaluateSingleCondition(
                    part,
                    media, mediana, desviacion,
                    tendencia, percentil90, studentsAtRisk
            )) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateSingleCondition(String condition,
                                            Double media,
                                            Double mediana,
                                            Double desviacion,
                                            String tendencia,
                                            Double percentil90,
                                            Integer studentsAtRisk) {

        String[] operators = {"<=", ">=", "==", "<", ">"};
        String operator = null;

        for (String op : operators) {
            if (condition.contains(op)) {
                operator = op;
                break;
            }
        }

        if (operator == null) return false;

        String[] tokens = condition.split(operator);
        String left = tokens[0].trim();
        String right = tokens[1].trim();

        Object leftValue = switch (left) {
            case "media" -> media;
            case "mediana" -> mediana;
            case "desviacion" -> desviacion;
            case "tendencia" -> tendencia;
            case "percentil90" -> percentil90;
            case "studentsAtRisk" -> studentsAtRisk;
            default -> null;
        };

        if (leftValue == null) return false;
        if (leftValue instanceof Number) {
            double leftNum = ((Number) leftValue).doubleValue();
            double rightNum = Double.parseDouble(right);

            return switch (operator) {
                case "<" -> leftNum < rightNum;
                case ">" -> leftNum > rightNum;
                case "<=" -> leftNum <= rightNum;
                case ">=" -> leftNum >= rightNum;
                case "==" -> leftNum == rightNum;
                default -> false;
            };
        }

        if (leftValue instanceof String) {
            return leftValue.toString().equalsIgnoreCase(right);
        }
        return false;
    }

    private double getSeverityWeight(String severity) {
        if (severity == null) return 0;
        return switch (severity.toUpperCase()) {
            case "HIGH" -> 30;
            case "MEDIUM" -> 15;
            case "LOW" -> 5;
            default -> 0;
        };
    }

    private String calculateRiskLevel(double score) {
        if (score >= 60) return "HIGH";
        if (score >= 30) return "MEDIUM";
        return "LOW";
    }
}
