package gt.edu.uinsight.report.service;

import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepositoryImpl;
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
                            new AnalyticsClientRepositoryImpl()));

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