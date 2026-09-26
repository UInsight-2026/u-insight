package gt.edu.uinsight.report.mock;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.gateway.ReportDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockSection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MockDataGatewayTest {
    private final ReportDataGateway gateway = new MockDataGateway();

    private ReportFilter period(String period) {
        return new ReportFilter(period, null, null, null, null, null);
    }

    @Test
    void deberiaSepararLasSeccionesPorPeriodo() {
        assertEquals(9, gateway.findSections(period("2026-2")).size());
        assertEquals(3, gateway.findSections(period("2026-1")).size());
        assertEquals(12, gateway.findSections(period(null)).size());
    }

    @Test
    void deberiaSepararLasAlertasPorPeriodo() {
        assertEquals(14, gateway.findAlerts(period("2026-2")).size());
        assertEquals(2, gateway.findAlerts(period("2026-1")).size());
        assertEquals(16, gateway.findAlerts(period(null)).size());
    }

    @Test
    void deberiaDevolverVacioParaUnPeriodoSinDatos() {
        assertTrue(gateway.findCourses(period("2025-1")).isEmpty());
        assertTrue(gateway.findSections(period("2025-1")).isEmpty());
        assertTrue(gateway.findAlerts(period("2025-1")).isEmpty());
    }

    @Test
    void deberiaCombinarFiltros() {
        var filter = new ReportFilter("2026-2", "PROG2", "DOC-101", "A", "HIGH", "NEW");
        assertEquals(1, gateway.findSections(filter).size());
        assertEquals(2, gateway.findAlerts(filter).size());
    }

    @Test
    void deberiaEncontrarElCursoHistoricoSinMezclarSusDatos() {
        var course = gateway.findCourseById(4L).orElseThrow();
        assertEquals("PROG1", course.getCode());
        assertEquals(100, course.getTotalStudents());
        assertEquals(3, gateway.findSectionsByCourseId(4L).size());
        assertEquals(3, gateway.findSectionsByCourseId(4L).stream().mapToInt(MockSection::getStudentsAtRisk).sum());
        assertEquals(1, gateway.findAlertsByCourseId(4L).stream().filter(MockAlert::isActive).count());
        assertEquals(5, gateway.findAlertsBySectionId(10L).stream().filter(MockAlert::isActive).count());
    }

    @Test
    void deberiaConservarLosIndicadoresDelPeriodoActual() {
        var sections = gateway.findSections(period("2026-2"));
        assertEquals(36, sections.stream().mapToInt(MockSection::getStudentsAtRisk).sum());
        assertEquals(4, sections.stream().filter(s -> "HIGH".equals(s.getRiskLevel())).count());
        assertEquals(12, gateway.findAlerts(period("2026-2")).stream().filter(MockAlert::isActive).count());
    }
}
