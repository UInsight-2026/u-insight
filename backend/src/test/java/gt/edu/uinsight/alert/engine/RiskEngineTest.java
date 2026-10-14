package gt.edu.uinsight.alert.engine;

import gt.edu.uinsight.alertrule.entity.AlertRule;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RiskEngineTest {

    @Test
    void evaluatesStudentsAtRiskRuleFromConsolidatedSummaryCount() {
        AlertRuleService alertRuleService = mock(AlertRuleService.class);
        AlertRule rule = new AlertRule();
        rule.setName("Varios estudiantes en riesgo");
        rule.setConditionExpression("studentsAtRisk > 2");
        rule.setSeverity("HIGH");
        rule.setActive(true);
        when(alertRuleService.getActiveRules()).thenReturn(List.of(rule));

        SectionSummary summary = new SectionSummary();
        summary.setStudentsAtRisk(3);

        RiskResult result = new RiskEngine(alertRuleService).evaluate(summary);

        assertEquals(1, result.getRulesTriggered());
        assertEquals(List.of("Varios estudiantes en riesgo"), result.getActivatedRules());
    }
}
