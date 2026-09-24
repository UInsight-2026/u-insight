package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.OverviewResponse;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import gt.edu.uinsight.report.mock.MockDataGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OverviewServiceTest {

    private final MockDataGateway gateway = new MockDataGateway();
    private final FilterValidator filterValidator = new FilterValidator();
    private final OverviewService overviewService = new OverviewService(gateway, filterValidator);

    @Test
    void deberiaCalcularElResumenGeneralSinFiltros() {
        ReportFilter sinFiltros = new ReportFilter(null, null, null, null, null, null);

        OverviewResponse response = overviewService.getOverview(sinFiltros);

        assertEquals(13, response.getActiveAlerts());
        assertEquals(4, response.getHighRiskSections());
        assertEquals(39, response.getStudentsAtRisk());
        // El calculo temporal compara cuatro secciones HIGH con cuatro LOW.
        assertEquals("STABLE", response.getOverallTrend());
    }

    @Test
    void deberiaFiltrarPorCurso() {
        ReportFilter soloProg2 = new ReportFilter(null, "PROG2", null, null, null, null);

        OverviewResponse response = overviewService.getOverview(soloProg2);

        // Secciones de PROG2: A (HIGH), B (MEDIUM), C (LOW) -> 1 en alto riesgo
        assertEquals(1, response.getHighRiskSections());
    }

    @Test
    void deberiaRechazarUnNivelDeRiesgoInvalido() {
        ReportFilter riskLevelInvalido = new ReportFilter(null, null, null, null, "INVALIDO", null);

        assertThrows(InvalidFilterException.class, () -> overviewService.getOverview(riskLevelInvalido));
    }
}
