package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.CourseReportResponse;
import gt.edu.uinsight.report.exception.ResourceNotFoundException;
import gt.edu.uinsight.report.mock.MockDataGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseReportServiceTest {

    private final MockDataGateway gateway = new MockDataGateway();
    private final FilterValidator filterValidator = new FilterValidator();
    private final CourseReportService courseReportService = new CourseReportService(gateway, filterValidator);

    @Test
    void deberiaDevolverElReporteDelCursoProgramacionII() {
        ReportFilter sinFiltros = new ReportFilter(null, null, null, null, null, null);

        CourseReportResponse response = courseReportService.getCourseReport(1L, sinFiltros);

        assertEquals("Programacion II", response.getCourseName());
        assertEquals(120, response.getStudents());
        // Secciones A(8) + B(4) + C(1) = 13 estudiantes en riesgo
        assertEquals(13, response.getStudentsAtRisk());
    }

    @Test
    void deberiaLanzar404SiElCursoNoExiste() {
        ReportFilter sinFiltros = new ReportFilter(null, null, null, null, null, null);

        assertThrows(ResourceNotFoundException.class,
                () -> courseReportService.getCourseReport(999L, sinFiltros));
    }
}
