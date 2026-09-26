package gt.edu.uinsight.report.service;

import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import gt.edu.uinsight.report.common.ReportLogger;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import gt.edu.uinsight.report.dto.response.OverviewResponse;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import gt.edu.uinsight.report.gateway.AnalyticsGateway;
import gt.edu.uinsight.report.gateway.B6AnalyticsGateway;
import gt.edu.uinsight.report.mock.MockDataGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

class OverviewServiceTest {

    private final MockDataGateway gateway = new MockDataGateway();
    private final FilterValidator filterValidator = new FilterValidator();
    private final ReportLogger reportLogger = new ReportLogger();

    // Servicio real de B6; solo se simulan sus dependencias, igual que en B6AnalyticsGatewayTest.
    private final AnalyticsGateway analyticsReal =
            new B6AnalyticsGateway(new SummaryService(repositorioDeB6()));

    private static AnalyticsClientRepository repositorioDeB6() {
        AnalyticsClientRepository repo = mock(AnalyticsClientRepository.class);
        when(repo.getTrend(anyLong())).thenReturn(new TrendResponse(
                TrendClassification.NEGATIVE, new BigDecimal("-2.5"), List.of()));
        return repo;
    }

    // Doble de prueba que simula a B6 caido, para probar la degradacion.
    private final AnalyticsGateway analyticsCaido = AnalyticsSnapshot::unavailable;

    private OverviewService servicioCon(AnalyticsGateway analytics) {
        return new OverviewService(gateway, analytics, filterValidator, reportLogger);
    }

    private ReportFilter filtroDePeriodo(String period) {
        return new ReportFilter(period, null, null, null, null, null);
    }

    @Test
    void deberiaCalcularElResumenGeneralSinFiltros() {
        OverviewResponse response = servicioCon(analyticsReal)
                .getOverview(filtroDePeriodo(null));

        // Los dos periodos juntos: 12 secciones, 16 alertas.
        assertEquals(13, response.getActiveAlerts());
        assertEquals(4, response.getHighRiskSections());
        assertEquals(39, response.getStudentsAtRisk());
        assertEquals("NEGATIVE", response.getOverallTrend());
        assertEquals("B6", response.getTrendSource());
        assertTrue(response.getUnavailableSources().isEmpty());
    }

    @Test
    void deberiaConservarLosIndicadoresDeLaSemana2AlFiltrarPorSuPeriodo() {
        OverviewResponse response = servicioCon(analyticsReal)
                .getOverview(filtroDePeriodo("2026-2"));

        assertEquals(12, response.getActiveAlerts());
        assertEquals(4, response.getHighRiskSections());
        assertEquals(36, response.getStudentsAtRisk());
    }

    @Test
    void deberiaAcotarLosIndicadoresAlPeriodoAnterior() {
        OverviewResponse response = servicioCon(analyticsReal)
                .getOverview(filtroDePeriodo("2026-1"));

        assertEquals(1, response.getActiveAlerts());
        assertEquals(0, response.getHighRiskSections());
        assertEquals(3, response.getStudentsAtRisk());
    }

    @Test
    void deberiaFiltrarPorCurso() {
        ReportFilter soloProg2 = new ReportFilter(null, "PROG2", null, null, null, null);

        OverviewResponse response = servicioCon(analyticsReal).getOverview(soloProg2);

        // Secciones de PROG2: A (HIGH), B (MEDIUM), C (LOW) -> 1 en alto riesgo
        assertEquals(1, response.getHighRiskSections());
    }

    @Test
    void deberiaUsarLaReglaLocalCuandoB6NoResponde() {
        OverviewResponse response = servicioCon(analyticsCaido)
                .getOverview(filtroDePeriodo("2026-1"));

        // 0 secciones HIGH contra 2 LOW -> la regla local dice POSITIVE,
        // en lugar del NEGATIVE que devolveria B6.
        assertEquals("POSITIVE", response.getOverallTrend());
        assertEquals("LOCAL_FALLBACK", response.getTrendSource());
        assertTrue(response.getUnavailableSources().contains("B6"));
        // El reporte se entrega igual: los indicadores siguen ahi.
        assertEquals(3, response.getStudentsAtRisk());
    }

    @Test
    void deberiaResponderSinTendenciaSiElFiltroNoDejaSecciones() {
        ReportFilter cursoInexistente =
                new ReportFilter(null, "NO-EXISTE", null, null, null, null);

        OverviewResponse response = servicioCon(analyticsReal).getOverview(cursoInexistente);

        assertEquals(0, response.getActiveAlerts());
        assertEquals("INSUFFICIENT_DATA", response.getOverallTrend());
        assertEquals("NONE", response.getTrendSource());
    }

    @Test
    void deberiaRechazarUnNivelDeRiesgoInvalido() {
        ReportFilter riskLevelInvalido =
                new ReportFilter(null, null, null, null, "INVALIDO", null);

        assertThrows(InvalidFilterException.class,
                () -> servicioCon(analyticsReal).getOverview(riskLevelInvalido));
    }
}
