package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.SectionReportResponse;
import gt.edu.uinsight.report.exception.ResourceNotFoundException;
import gt.edu.uinsight.report.mock.MockDataGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SectionReportServiceTest {

    private final MockDataGateway gateway = new MockDataGateway();
    private final FilterValidator filterValidator = new FilterValidator();
    private final SectionReportService sectionReportService = new SectionReportService(gateway, filterValidator);

    @Test
    void deberiaDevolverElReporteDeLaSeccionA() {
        ReportFilter sinFiltros = new ReportFilter(null, null, null, null, null, null);

        SectionReportResponse response = sectionReportService.getSectionReport(10L, sinFiltros);

        assertEquals("A", response.getSectionName());
        assertEquals("HIGH", response.getRiskLevel());
        assertEquals(5, response.getActiveAlerts());
    }

    @Test
    void deberiaLanzar404SiLaSeccionNoExiste() {
        ReportFilter sinFiltros = new ReportFilter(null, null, null, null, null, null);

        assertThrows(ResourceNotFoundException.class,
                () -> sectionReportService.getSectionReport(999L, sinFiltros));
    }
}
