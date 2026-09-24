package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AlertReportResponse;
import gt.edu.uinsight.report.mock.MockDataGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertReportServiceTest {

    private final MockDataGateway gateway = new MockDataGateway();
    private final FilterValidator filterValidator = new FilterValidator();
    private final AlertReportService alertReportService = new AlertReportService(gateway, filterValidator);

    @Test
    void deberiaDevolverTodasLasAlertasSinFiltros() {
        ReportFilter sinFiltros = new ReportFilter(null, null, null, null, null, null);

        AlertReportResponse response = alertReportService.getAlerts(sinFiltros);

        assertEquals(16, response.getTotal());
    }

    @Test
    void deberiaFiltrarPorCursoYSeccion() {
        ReportFilter filtro = new ReportFilter(null, "PROG2", null, "A", null, null);

        AlertReportResponse response = alertReportService.getAlerts(filtro);

        assertEquals(5, response.getTotal());
        assertTrue(response.getAlerts().stream().allMatch(a -> a.getCourseCode().equals("PROG2")));
    }

    @Test
    void deberiaDevolverListaVaciaParaUnDocenteSinAlertas() {
        ReportFilter filtro = new ReportFilter(null, null, "DOC123", null, null, null);

        AlertReportResponse response = alertReportService.getAlerts(filtro);

        assertEquals(0, response.getTotal());
    }
}
