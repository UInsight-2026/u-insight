package gt.edu.uinsight.alert.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gt.edu.uinsight.alertrule.entity.AlertRule;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;

public class RiskEngine {

    private final AlertRuleService alertRuleService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RiskEngine(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    public RiskResult evaluate(SectionSummary summary) {
        if (summary == null) {
            throw new IllegalArgumentException("El resumen analítico de la sección es obligatorio.");
        }

        List<AlertRule> rules = alertRuleService.getActiveRules();
        List<String> activatedRules = new ArrayList<>();

        int rulesEvaluated = 0;
        int rulesTriggered = 0;
        double score = 0;
        boolean highSeverityTriggered = false;

        Map<String, Object> centralTendency = convert(summary.getCentralTendencyData());
        Map<String, Object> position = convert(summary.getPositionData());
        Map<String, Object> dispersion = convert(summary.getDispersionData());
        Map<String, Object> trend = convert(summary.getTrendData());

        Double mean = getDouble(centralTendency, "mean");
        Double median = getDouble(centralTendency, "median");
        Double standardDeviation = getDouble(dispersion, "standardDeviation");
        String trendClassification = firstString(trend, "classification", "value");
        Double percentile90 = getPercentile90(position);
        Integer studentsAtRisk = positiveSampleSize(centralTendency)
                ? summary.getStudentsAtRisk()
                : null;
        Double studentsAtRiskPercentage = positiveSampleSize(centralTendency)
                ? summary.getStudentsAtRiskPercentage()
                : null;

        for (AlertRule rule : rules) {
            if (!Boolean.TRUE.equals(rule.getActive())) {
                continue;
            }

            Boolean triggered = evaluateRule(
                    rule.getConditionExpression(),
                    mean,
                    median,
                    standardDeviation,
                    trendClassification,
                    percentile90,
                    studentsAtRisk,
                    studentsAtRiskPercentage);
            if (triggered == null) {
                continue;
            }

            rulesEvaluated++;
            if (triggered) {
                activatedRules.add(rule.getName());
                rulesTriggered++;
                score += getSeverityWeight(rule.getSeverity());
                highSeverityTriggered |= "HIGH".equalsIgnoreCase(rule.getSeverity());
            }
        }

        String riskLevel = calculateRiskLevel(score, highSeverityTriggered);
        return new RiskResult(
                riskLevel,
                score,
                rulesEvaluated,
                rulesTriggered,
                0,
                activatedRules);
    }

    private Map<String, Object> convert(Object obj) {
        if (obj == null) {
            return null;
        }
        return objectMapper.convertValue(obj, new TypeReference<>() {});
    }

    private boolean positiveSampleSize(Map<String, Object> centralTendency) {
        Double sampleSize = getDouble(centralTendency, "sampleSize");
        return sampleSize != null && sampleSize > 0;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }
        Object value = map.get(key);
        return value instanceof Number number ? number.doubleValue() : null;
    }

    private String firstString(Map<String, Object> map, String... keys) {
        if (map == null) {
            return null;
        }
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    private Double getPercentile90(Map<String, Object> position) {
        if (position == null || !(position.get("percentiles") instanceof Map<?, ?> percentiles)) {
            return null;
        }
        Object value = percentiles.get("P90");
        if (value == null) {
            value = percentiles.get("percentile90");
        }
        return value instanceof Number number ? number.doubleValue() : null;
    }

    private Boolean evaluateRule(
            String expression,
            Double mean,
            Double median,
            Double standardDeviation,
            String trend,
            Double percentile90,
            Integer studentsAtRisk,
            Double studentsAtRiskPercentage) {
        if (expression == null || expression.isBlank()) {
            return false;
        }

        boolean hasUnavailableValue = false;
        for (String condition : expression.split("&&")) {
            Boolean result = evaluateCondition(
                    condition.trim(),
                    mean,
                    median,
                    standardDeviation,
                    trend,
                    percentile90,
                    studentsAtRisk,
                    studentsAtRiskPercentage);
            if (Boolean.FALSE.equals(result)) {
                return false;
            }
            if (result == null) {
                hasUnavailableValue = true;
            }
        }
        return hasUnavailableValue ? null : true;
    }

    private Boolean evaluateCondition(
            String condition,
            Double mean,
            Double median,
            Double standardDeviation,
            String trend,
            Double percentile90,
            Integer studentsAtRisk,
            Double studentsAtRiskPercentage) {
        String operator = null;
        for (String candidate : new String[] {"<=", ">=", "==", "<", ">"}) {
            if (condition.contains(candidate)) {
                operator = candidate;
                break;
            }
        }
        if (operator == null) {
            return false;
        }

        String[] tokens = condition.split(java.util.regex.Pattern.quote(operator), 2);
        if (tokens.length != 2) {
            return false;
        }

        String left = tokens[0].trim();
        String right = tokens[1].trim();
        Object leftValue = switch (left) {
            case "media", "mean", "average" -> mean;
            case "mediana", "median" -> median;
            case "desviacion", "standardDeviation", "dispersion" -> standardDeviation;
            case "tendencia", "trend", "classification" -> trend;
            case "percentil90", "percentile90" -> percentile90;
            case "studentsAtRisk" -> studentsAtRisk;
            case "studentsAtRiskPercentage", "percentageStudentsAtRisk",
                    "failing_rate", "failingRate", "failure_rate" ->
                    studentsAtRiskPercentage;
            default -> null;
        };
        if (leftValue == null) {
            return null;
        }

        if (leftValue instanceof Number number) {
            final double rightValue;
            try {
                rightValue = Double.parseDouble(right);
            } catch (NumberFormatException exception) {
                return false;
            }
            double leftNumber = number.doubleValue();
            return switch (operator) {
                case "<" -> leftNumber < rightValue;
                case ">" -> leftNumber > rightValue;
                case "<=" -> leftNumber <= rightValue;
                case ">=" -> leftNumber >= rightValue;
                case "==" -> Double.compare(leftNumber, rightValue) == 0;
                default -> false;
            };
        }

        if (leftValue instanceof String value && "==".equals(operator)) {
            return value.equalsIgnoreCase(right);
        }
        return false;
    }

    private double getSeverityWeight(String severity) {
        if (severity == null) {
            return 0;
        }
        return switch (severity.toUpperCase()) {
            case "HIGH" -> 30;
            case "MEDIUM" -> 15;
            case "LOW" -> 5;
            default -> 0;
        };
    }

    private String calculateRiskLevel(double score, boolean highSeverityTriggered) {
        if (highSeverityTriggered || score >= 60) {
            return "HIGH";
        }
        if (score >= 30) {
            return "MEDIUM";
        }
        return "LOW";
    }
}
