package gt.edu.uinsight.alert.service;

import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.repository.AlertRepository;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RiskEvaluationServiceTest {

    private static final Long SECTION_ID = 10L;

    @Test
    void savesOneAlertPerTriggeredRuleAndReturnsTheGeneratedCount() {
        RiskEvaluationService service = service(new RiskResult(
                "HIGH", 30.0, 2, 1, 1, List.of("Promedio bajo con tendencia negativa")));
        when(alertRepository.existsBySectionIdAndAlertTypeAndTitleAndStatusNotIn(
                anyInt(), anyString(), anyString(), anyCollection())).thenReturn(false);

        var response = service.evaluateSectionRisk(SECTION_ID);

        assertEquals(1, response.getAlertsGenerated());
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void doesNotSaveDuplicateActiveRuleAlert() {
        RiskEvaluationService service = service(new RiskResult(
                "HIGH", 30.0, 2, 1, 1, List.of("Promedio bajo con tendencia negativa")));
        when(alertRepository.existsBySectionIdAndAlertTypeAndTitleAndStatusNotIn(
                anyInt(), anyString(), anyString(), anyCollection())).thenReturn(true);

        var response = service.evaluateSectionRisk(SECTION_ID);

        assertEquals(1, response.getRulesTriggered());
        assertEquals(0, response.getAlertsGenerated());
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void refusesToPersistWhenNoRulesCouldBeEvaluated() {
        RiskEvaluationService service = service(new RiskResult("LOW", 0.0, 0, 0, 0, List.of()));

        assertThrows(ResponseStatusException.class, () -> service.evaluateSectionRisk(SECTION_ID));
        verify(alertRepository, never()).save(any(Alert.class));
    }

    private final SummaryService summaryService = mock(SummaryService.class);
    private final AlertRepository alertRepository = mock(AlertRepository.class);
    private final RiskEngine riskEngine = mock(RiskEngine.class);

    private RiskEvaluationService service(RiskResult result) {
        when(summaryService.getSummary(SECTION_ID)).thenReturn(new SectionSummary());
        when(riskEngine.evaluate(any(SectionSummary.class))).thenReturn(result);
        return new RiskEvaluationService(riskEngine, summaryService, alertRepository);
    }
}
