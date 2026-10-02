package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class B6AnalyticsGatewayTest {
    private final AnalyticsClientRepository repository = mock(AnalyticsClientRepository.class);
    // Se ejecuta el servicio real de B6; solo se simulan sus dependencias.
    private final B6AnalyticsGateway gateway = new B6AnalyticsGateway(new SummaryService(repository));

    @Test
    void deberiaLeerLaAnaliticaDeB6ConElContratoActual() {
        when(repository.getCentralTendency(10L))
                .thenReturn(new CentralTendencyResponse(30, 72.5, 73.0, List.of()));
        when(repository.getDispersion(10L))
                .thenReturn(new DispersionResponse(10L, null, 40.0, 95.0, 55.0, 124.55, 11.16, null));
        when(repository.getTrend(10L)).thenReturn(new TrendResponse(
                TrendClassification.NEGATIVE, new BigDecimal("-2.5"), List.of()));

        var snapshot = gateway.getSectionAnalytics(10L);

        assertTrue(snapshot.isAvailable());
        assertEquals(10L, snapshot.getSectionId());
        assertEquals(72.5, snapshot.getMean());
        assertEquals(73.0, snapshot.getMedian());
        assertEquals(30, snapshot.getSampleSize());
        assertEquals(11.16, snapshot.getStandardDeviation());
        assertEquals("NEGATIVE", snapshot.getTrendClassification());
        assertEquals(-2.5, snapshot.getAverageChange());
        verify(repository).getTrend(10L);
    }

    @Test
    void deberiaDetectarQueB6CapturoElFalloDeSusComponentes() {
        when(repository.getCentralTendency(10L)).thenThrow(new IllegalStateException("sin conexion"));
        when(repository.getDispersion(10L)).thenThrow(new IllegalStateException("sin conexion"));
        when(repository.getTrend(10L)).thenThrow(new IllegalStateException("sin conexion"));
        var snapshot = gateway.getSectionAnalytics(10L);
        assertFalse(snapshot.isAvailable());
        assertEquals(10L, snapshot.getSectionId());
        assertNull(snapshot.getMean());
        assertNull(snapshot.getTrendClassification());
    }

    @Test
    void deberiaConservarLosDatosDisponiblesCuandoFallaLaTendencia() {
        when(repository.getCentralTendency(10L))
                .thenReturn(new CentralTendencyResponse(30, 72.5, 73.0, List.of()));
        when(repository.getTrend(10L)).thenThrow(new IllegalStateException("B4 no responde"));
        var snapshot = gateway.getSectionAnalytics(10L);
        assertTrue(snapshot.isAvailable());
        assertEquals(72.5, snapshot.getMean());
        assertNull(snapshot.getTrendClassification());
        assertNull(snapshot.getAverageChange());
        assertNull(snapshot.getStandardDeviation());
    }

    @Test
    void deberiaTolerarRespuestaNulaYExcepcionDelServicio() {
        var service = mock(SummaryService.class);
        var adapter = new B6AnalyticsGateway(service);
        when(service.getSummary(10L)).thenReturn(null).thenThrow(new IllegalStateException("B6 no responde"));
        assertFalse(adapter.getSectionAnalytics(10L).isAvailable());
        assertFalse(adapter.getSectionAnalytics(10L).isAvailable());
    }

    @Test
    void deberiaTolerarCamposNulosEnLaTendencia() {
        var service = mock(SummaryService.class);
        var summary = new SectionSummary();
        summary.setTrendData(new TrendResponse(null, null, List.of()));
        when(service.getSummary(10L)).thenReturn(summary);
        var snapshot = new B6AnalyticsGateway(service).getSectionAnalytics(10L);
        assertNull(snapshot.getTrendClassification());
        assertNull(snapshot.getAverageChange());
    }
}
