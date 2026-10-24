//semana 3
package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.common.ReportLogger;
import gt.edu.uinsight.report.dto.filter.PageFilter;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AlertReportResponse;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import gt.edu.uinsight.report.mock.MockDataGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertReportServiceTest {

    private final MockDataGateway gateway = new MockDataGateway();
    private final FilterValidator filterValidator = new FilterValidator();
    private final ReportLogger reportLogger = new ReportLogger();
    private final AlertReportService alertReportService =
            new AlertReportService(gateway, filterValidator, reportLogger);

    private final ReportFilter sinFiltros =
            new ReportFilter(null, null, null, null, null, null);

    private PageFilter pagina(Integer page, Integer size, String sort) {
        return new PageFilter(page, size, sort);
    }

    @Test
    void deberiaDevolverTodasLasAlertasSinFiltros() {
        AlertReportResponse response =
                alertReportService.getAlerts(sinFiltros, pagina(null, null, null));

        // 14 alertas de 2026-2 más 2 de 2026-1
        assertEquals(16, response.getTotal());
        assertEquals(16, response.getAlerts().size());
        assertEquals(1, response.getTotalPages());
    }

    @Test
    void deberiaPartirElResultadoEnPaginas() {
        AlertReportResponse primera =
                alertReportService.getAlerts(sinFiltros, pagina(0, 5, null));
        assertEquals(16, primera.getTotal());
        assertEquals(4, primera.getTotalPages());
        assertEquals(5, primera.getAlerts().size());

        AlertReportResponse ultima =
                alertReportService.getAlerts(sinFiltros, pagina(3, 5, null));
        // 16 alertas en páginas de 5: la cuarta página trae solo una
        assertEquals(1, ultima.getAlerts().size());
    }

    @Test
    void deberiaDevolverPaginaVaciaSiSePideUnaPaginaFueraDeRango() {
        AlertReportResponse response =
                alertReportService.getAlerts(sinFiltros, pagina(99, 5, null));
        assertTrue(response.getAlerts().isEmpty());
        // El total no cambia: sirve para que C6 sepa volver a la primera página.
        assertEquals(16, response.getTotal());
        assertEquals(4, response.getTotalPages());
    }

    @Test
    void deberiaOrdenarPorFecha() {
        AlertReportResponse recientes =
                alertReportService.getAlerts(sinFiltros, pagina(0, 20, "NEWEST"));
        AlertReportResponse antiguas =
                alertReportService.getAlerts(sinFiltros, pagina(0, 20, "OLDEST"));

        assertEquals(1012L, recientes.getAlerts().get(0).getId());
        assertEquals(1015L, antiguas.getAlerts().get(0).getId());
    }

    @Test
    void deberiaOrdenarPorRiesgo() {
        AlertReportResponse response =
                alertReportService.getAlerts(sinFiltros, pagina(0, 20, "RISK"));

        assertEquals("HIGH", response.getAlerts().get(0).getRiskLevel());
        // A igual riesgo manda la más reciente: 1005 es la HIGH más nueva.
        assertEquals(1005L, response.getAlerts().get(0).getId());
    }

    @Test
    void deberiaFiltrarPorCursoYSeccion() {
        ReportFilter filtro = new ReportFilter(null, "PROG2", null, "A", null, null);
        AlertReportResponse response =
                alertReportService.getAlerts(filtro, pagina(null, null, null));

        assertEquals(5, response.getTotal());
        assertTrue(response.getAlerts().stream()
                .allMatch(a -> a.getCourseCode().equals("PROG2")));
    }

    @Test
    void deberiaFiltrarPorEstadoDeAlerta() {
        ReportFilter prog2Nuevas = new ReportFilter(null, "PROG2", null, null, null, "NEW");
        AlertReportResponse response =
                alertReportService.getAlerts(prog2Nuevas, pagina(null, null, null));

        // Alertas 1001, 1003 y 1005 de la sección A más la 1006 de la B
        assertEquals(4, response.getTotal());
    }

    @Test
    void deberiaDevolverListaVaciaParaUnDocenteSinAlertas() {
        ReportFilter filtro = new ReportFilter(null, null, "DOC-999", null, null, null);
        AlertReportResponse response =
                alertReportService.getAlerts(filtro, pagina(null, null, null));

        assertEquals(0, response.getTotal());
        assertEquals(0, response.getTotalPages());
        assertTrue(response.getAlerts().isEmpty());
    }

    @Test
    void deberiaRechazarParametrosDePaginacionInvalidos() {
        assertThrows(InvalidFilterException.class,
                () -> alertReportService.getAlerts(sinFiltros, pagina(0, 0, null)));
        assertThrows(InvalidFilterException.class,
                () -> alertReportService.getAlerts(sinFiltros, pagina(0, 10, "ALFABETICO")));
    }
}