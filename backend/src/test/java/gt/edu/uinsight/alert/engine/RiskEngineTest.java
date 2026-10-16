package gt.edu.uinsight.alert.engine;

import gt.edu.uinsight.alertrule.entity.AlertRule;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RiskEngineTest {

    @Test
    void readsTrendClassificationAndUsesHighSeverityAsHighRisk() {
        RiskResult result = evaluate(
                "media < 60 && tendencia == NEGATIVE",
                "HIGH",
                completeSummary());

        assertEquals(1, result.getRulesEvaluated());
        assertEquals(1, result.getRulesTriggered());
        assertEquals("HIGH", result.getRiskLevel());
        assertEquals(List.of("Promedio bajo con tendencia negativa"), result.getActivatedRules());
    }

    @Test
    void readsP90FromPositionPercentiles() {
        RiskResult result = evaluate("percentil90 >= 90", "MEDIUM", completeSummary());

        assertEquals(1, result.getRulesTriggered());
        assertEquals(List.of("Promedio bajo con tendencia negativa"), result.getActivatedRules());
    }

    @Test
    void evaluatesRiskPercentageRatherThanRiskCount() {
        RiskResult result = evaluate(
                "studentsAtRiskPercentage >= 25",
                "MEDIUM",
                completeSummary());

        assertEquals(1, result.getRulesTriggered());
    }

    @Test
    void evaluatesConfiguredRulesUsingSectionSummaryFields() {
        RiskResult result = evaluateRules(
                List.of(
                        rule("Promedio bajo", "average < 60 && trend == NEGATIVE", "HIGH"),
                        rule("Dispersion alta", "dispersion > 30", "MEDIUM"),
                        rule("Alta reprobacion", "studentsAtRiskPercentage > 40", "HIGH")),
                completeSummary());

        assertEquals(3, result.getRulesEvaluated());
        assertEquals(1, result.getRulesTriggered());
        assertEquals(List.of("Promedio bajo"), result.getActivatedRules());
    }

    @Test
    void evaluatesConfiguredDispersionAndFailureRateAliases() {
        SectionSummary summary = completeSummary();
        summary.setDispersionData(new DispersionResponse(
                10L, null, new BigDecimal("10"), new BigDecimal("90"),
                new BigDecimal("80"), new BigDecimal("1600"),
                new BigDecimal("40"), DispersionClassification.HIGH_DISPERSION));
        summary.setStudentsAtRiskPercentage(50.0);

        RiskResult result = evaluateRules(
                List.of(
                        rule("Promedio bajo", "average < 60 && trend == NEGATIVE", "HIGH"),
                        rule("Dispersion alta", "dispersion > 30", "MEDIUM"),
                        rule("Alta reprobacion", "studentsAtRiskPercentage > 40", "HIGH")),
                summary);

        assertEquals(3, result.getRulesEvaluated());
        assertEquals(3, result.getRulesTriggered());
        assertEquals(List.of("Promedio bajo", "Dispersion alta", "Alta reprobacion"),
                result.getActivatedRules());
    }

    @Test
    void skipsRuleWhenRequiredDataIsUnavailable() {
        SectionSummary summary = completeSummary();
        summary.setTrendData(null);

        RiskResult result = evaluate(
                "media < 60 && tendencia == NEGATIVE",
                "HIGH",
                summary);

        assertEquals(0, result.getRulesEvaluated());
        assertEquals(0, result.getRulesTriggered());
        assertEquals("LOW", result.getRiskLevel());
    }

    private RiskResult evaluate(String expression, String severity, SectionSummary summary) {
        return evaluateRules(List.of(rule("Promedio bajo con tendencia negativa", expression, severity)),
                summary);
    }

    private RiskResult evaluateRules(List<AlertRule> rules, SectionSummary summary) {
        AlertRuleService alertRuleService = mock(AlertRuleService.class);
        when(alertRuleService.getActiveRules()).thenReturn(rules);
        return new RiskEngine(alertRuleService).evaluate(summary);
    }

    private AlertRule rule(String name, String expression, String severity) {
        AlertRule rule = new AlertRule();
        rule.setName(name);
        rule.setConditionExpression(expression);
        rule.setSeverity(severity);
        rule.setActive(true);
        return rule;
    }

    private SectionSummary completeSummary() {
        SectionSummary summary = new SectionSummary();
        summary.setCentralTendencyData(new CentralTendencyResponse(10, 55.0, 54.0, List.of()));
        summary.setPositionData(new SectionPositionResponse(
                10L, 10, Map.of("Q1", 45.0, "Q2", 55.0, "Q3", 75.0), Map.of("P90", 90.0)));
        summary.setDispersionData(new DispersionResponse(
                10L, null, new BigDecimal("10"), new BigDecimal("90"),
                new BigDecimal("80"), new BigDecimal("100"),
                new BigDecimal("10"), DispersionClassification.MODERATE_DISPERSION));
        summary.setTrendData(new TrendResponse(
                TrendClassification.NEGATIVE, new BigDecimal("-5"), List.of()));
        summary.setStudentsAtRisk(2);
        summary.setStudentsAtRiskPercentage(25.0);
        return summary;
    }
}
