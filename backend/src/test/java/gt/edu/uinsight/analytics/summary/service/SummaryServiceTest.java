package gt.edu.uinsight.analytics.summary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.summary.dto.external.PositionData;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    private static final Long SECTION_ID = 101L;

    @Mock
    private AnalyticsClientRepository analyticsClientRepository;

    @InjectMocks
    private SummaryService summaryService;

    // ------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------

    @Test
    @DisplayName("1. Todo disponible: retorna el resumen completo sin componentes no disponibles")
    void getSummary_AllAvailable_Success() {
        stubCentralTendencyAvailable();
        stubPositionAvailable();
        stubDispersionAvailable();
        stubTrendAvailable();
        stubStudentComparisonAvailable();

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result);
        assertEquals(SECTION_ID, result.getSectionId());
        assertTrue(result.getUnavailableComponents().isEmpty());
    }

    @Test
    @DisplayName("2. Fallo parcial: cuando un componente falla, se agrega a unavailableComponents sin romper la respuesta")
    void getSummary_PartialFailure_SuccessWithUnavailableComponent() {
        stubCentralTendencyAvailable();
        stubPositionAvailable();
        stubDispersionAvailable();
        stubStudentComparisonAvailable();
        when(analyticsClientRepository.getTrend(SECTION_ID))
                .thenThrow(new RuntimeException("Error de conexión"));

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result);
        assertEquals(1, result.getUnavailableComponents().size());
        assertTrue(result.getUnavailableComponents().contains("trend"));
    }

    @Test
    @DisplayName("3. Fallo total / Datos insuficientes: cuando todos devuelven null o error")
    void getSummary_TotalFailure_ReturnsSummaryWithAllUnavailable() {
        when(analyticsClientRepository.getCentralTendency(SECTION_ID)).thenReturn(null);
        when(analyticsClientRepository.getPosition(SECTION_ID)).thenReturn(null);
        when(analyticsClientRepository.getDispersion(SECTION_ID)).thenThrow(new RuntimeException("Error"));
        when(analyticsClientRepository.getTrend(SECTION_ID)).thenThrow(new RuntimeException("Error"));
        when(analyticsClientRepository.getStudentComparison(SECTION_ID)).thenThrow(new RuntimeException("Error"));

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result);
        assertEquals(5, result.getUnavailableComponents().size());
    }

    // ------------------------------------------------------------------
    // Helpers: cada uno crea el mock antes del stubbing para evitar
    // mock() dentro de thenReturn() (UnfinishedStubbingException).
    // ------------------------------------------------------------------

    private void stubCentralTendencyAvailable() {
        CentralTendencyResponse response = mock(CentralTendencyResponse.class);
        when(analyticsClientRepository.getCentralTendency(SECTION_ID)).thenReturn(response);
    }

    private void stubPositionAvailable() {
        PositionData data = mock(PositionData.class);
        when(analyticsClientRepository.getPosition(SECTION_ID)).thenReturn(data);
    }

    private void stubDispersionAvailable() {
        DispersionResponse response = mock(DispersionResponse.class);
        when(analyticsClientRepository.getDispersion(SECTION_ID)).thenReturn(response);
    }

    private void stubTrendAvailable() {
        TrendResponse response = mock(TrendResponse.class);
        when(analyticsClientRepository.getTrend(SECTION_ID)).thenReturn(response);
    }

    private void stubStudentComparisonAvailable() {
        StudentComparisonResponse response = mock(StudentComparisonResponse.class);
        when(analyticsClientRepository.getStudentComparison(SECTION_ID)).thenReturn(response);
    }
}