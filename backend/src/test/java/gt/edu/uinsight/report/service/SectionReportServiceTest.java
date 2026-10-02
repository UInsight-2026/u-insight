package gt.edu.uinsight.report.service;

import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;
import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import gt.edu.uinsight.report.common.ReportLogger;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import gt.edu.uinsight.report.dto.response.SectionReportResponse;
import gt.edu.uinsight.report.exception.ResourceNotFoundException;
import gt.edu.uinsight.report.gateway.AnalyticsGateway;
import gt.edu.uinsight.report.gateway.B6AnalyticsGateway;
import gt.edu.uinsight.report.mock.MockDataGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SectionReportServiceTest {

    private final MockDataGateway gateway = new MockDataGateway();
    private final FilterValidator filterValidator = new FilterValidator();
    private final ReportLogger reportLogger = new ReportLogger();

    // Integración real con B6.
    private final AnalyticsGateway analyticsReal =
            new B6AnalyticsGateway(
                    new SummaryService(
                            repositorioDeB6()));

    // Servicio real de B6 con sus dependencias simuladas, sin base de datos.
    private static AnalyticsClientRepository repositorioDeB6() {
        var repository = mock(AnalyticsClientRepository.class);
        when(repository.getCentralTendency(10L))
                .thenReturn(new CentralTendencyResponse(30, 72.5, 73.0, List.of()));
        when(repository.getDispersion(10L))
                .thenReturn(new DispersionResponse(10L, null, 40.0, 95.0, 55.0, 124.55, 11.16, null));
        when(repository.getTrend(10L)).thenReturn(new TrendResponse(
                TrendClassification.NEGATIVE, new BigDecimal("-2.5"), List.of()));
        return repository;
    }

    // Simula que B6 no está disponible.
    private final AnalyticsGateway analyticsCaido =
            AnalyticsSnapshot::unavailable;

    private final ReportFilter sinFiltros =
            new ReportFilter(null, null, null, null, null, null);

    private SectionReportService servicioCon(AnalyticsGateway analytics) {
        return new SectionReportService(
                gateway,
                analytics,
                filterValidator,
                reportLogger);
    }

    @Test
    void deberiaDevolverElReporteDeLaSeccionA() {

        SectionReportResponse response =
                servicioCon(analyticsReal)
                        .getSectionReport(10L, sinFiltros);

        assertEquals("A", response.getSectionName());
        assertEquals("HIGH", response.getRiskLevel());
        assertEquals(5, response.getActiveAlerts());
        assertEquals(8, response.getStudentsAtRisk());
    }

    @Test
    void deberiaTraerLosIndicadoresDeB6() {

        SectionReportResponse response =
                servicioCon(analyticsReal)
                        .getSectionReport(10L, sinFiltros);

        assertTrue(response.getAnalytics().isAvailable());
        assertEquals(72.5, response.getAnalytics().getMean());
        assertEquals(11.16, response.getAnalytics().getStandardDeviation());
        assertEquals(
                "NEGATIVE",
                response.getAnalytics().getTrendClassification());
    }

    @Test
    void deberiaEntregarElReporteAunqueB6NoResponda() {

        SectionReportResponse response =
                servicioCon(analyticsCaido)
                        .getSectionReport(10L, sinFiltros);

        // Lo propio de C5 sigue completo.
        assertEquals("A", response.getSectionName());
        assertEquals(5, response.getActiveAlerts());

        // El bloque de B6 queda marcado como no disponible.
        assertFalse(response.getAnalytics().isAvailable());
    }

    @Test
    void deberiaLanzar404SiLaSeccionNoExiste() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> servicioCon(analyticsReal)
                        .getSectionReport(999L, sinFiltros));
    }
}
