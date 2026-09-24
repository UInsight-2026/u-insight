package gt.edu.uinsight.analytics.summary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gt.edu.uinsight.analytics.summary.dto.external.CentralTendencyData;
import gt.edu.uinsight.analytics.summary.dto.external.DispersionData;
import gt.edu.uinsight.analytics.summary.dto.external.PositionData;
import gt.edu.uinsight.analytics.summary.dto.external.StudentComparisonData;
import gt.edu.uinsight.analytics.summary.dto.external.TrendData;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private AnalyticsClientRepository analyticsClientRepository;

    @InjectMocks
    private SummaryService summaryService;

    private Long sectionId;

    @BeforeEach
    void setUp() {
        sectionId = 101L;
    }

    @Test
    @DisplayName("1. Todo disponible: retorna el resumen completo sin componentes no disponibles")
    void getSummary_AllAvailable_Success() {
        when(analyticsClientRepository.getCentralTendency(sectionId)).thenReturn(mock(CentralTendencyData.class));
        when(analyticsClientRepository.getPosition(sectionId)).thenReturn(mock(PositionData.class));
        when(analyticsClientRepository.getDispersion(sectionId)).thenReturn(mock(DispersionData.class));
        when(analyticsClientRepository.getTrend(sectionId)).thenReturn(mock(TrendData.class));
        when(analyticsClientRepository.getStudentComparison(sectionId)).thenReturn(mock(StudentComparisonData.class));

        SectionSummary result = summaryService.getSummary(sectionId);

        assertNotNull(result);
        assertEquals(sectionId, result.getSectionId());
        assertTrue(result.getUnavailableComponents().isEmpty());
    }

    @Test
    @DisplayName("2. Fallo parcial: cuando un componente falla, se agrega a unavailableComponents sin romper la respuesta")
    void getSummary_PartialFailure_SuccessWithUnavailableComponent() {
        when(analyticsClientRepository.getCentralTendency(sectionId)).thenReturn(mock(CentralTendencyData.class));
        when(analyticsClientRepository.getPosition(sectionId)).thenReturn(mock(PositionData.class));
        when(analyticsClientRepository.getDispersion(sectionId)).thenReturn(mock(DispersionData.class));
        when(analyticsClientRepository.getTrend(sectionId)).thenThrow(new RuntimeException("Error de conexión"));
        when(analyticsClientRepository.getStudentComparison(sectionId)).thenReturn(mock(StudentComparisonData.class));

        SectionSummary result = summaryService.getSummary(sectionId);

        assertNotNull(result);
        assertEquals(1, result.getUnavailableComponents().size());
        assertTrue(result.getUnavailableComponents().contains("trend"));
    }

    @Test
    @DisplayName("3. Fallo total / Datos insuficientes: cuando todos devuelven null o error")
    void getSummary_TotalFailure_ReturnsSummaryWithAllUnavailable() {
        when(analyticsClientRepository.getCentralTendency(anyLong())).thenReturn(null);
        when(analyticsClientRepository.getPosition(anyLong())).thenReturn(null);
        when(analyticsClientRepository.getDispersion(anyLong())).thenThrow(new RuntimeException("Error"));
        when(analyticsClientRepository.getTrend(anyLong())).thenThrow(new RuntimeException("Error"));
        when(analyticsClientRepository.getStudentComparison(anyLong())).thenThrow(new RuntimeException("Error"));

        SectionSummary result = summaryService.getSummary(sectionId);

        assertNotNull(result);
        assertEquals(5, result.getUnavailableComponents().size());
    }
}   