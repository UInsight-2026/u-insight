package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.common.ReportLogger;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.CourseReportResponse;
import gt.edu.uinsight.report.dto.response.CourseSectionItemResponse;
import gt.edu.uinsight.report.exception.ResourceNotFoundException;
import gt.edu.uinsight.report.gateway.ReportDataGateway;
import gt.edu.uinsight.report.mock.MockDataGateway;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseReportServiceTest {

    private final ReportDataGateway gateway = new MockDataGateway();
    private final FilterValidator filterValidator = new FilterValidator();
    private final ReportLogger reportLogger = new ReportLogger();

    private final CourseReportService courseReportService =
            new CourseReportService(gateway, filterValidator, reportLogger);

    @Test
    void deberiaDevolverElReporteDelCursoProgramacionII() {

        ReportFilter sinFiltros =
                new ReportFilter(null, null, null, null, null, null);

        CourseReportResponse response =
                courseReportService.getCourseReport(1L, sinFiltros);

        assertEquals("Programacion II", response.getCourseName());
        assertEquals(120, response.getStudents());
        assertEquals(13, response.getStudentsAtRisk());
        assertEquals(6, response.getActiveAlerts());
    }

    @Test
    void deberiaDevolverElDesgloseDeLasSecciones() {

        ReportFilter sinFiltros =
                new ReportFilter(null, null, null, null, null, null);

        CourseReportResponse response =
                courseReportService.getCourseReport(1L, sinFiltros);

        assertEquals(3, response.getSections().size());

        CourseSectionItemResponse primeraSeccion =
                response.getSections().get(0);

        assertEquals(10L, primeraSeccion.getSectionId());
        assertEquals("A", primeraSeccion.getSectionName());
        assertEquals("HIGH", primeraSeccion.getRiskLevel());
        assertEquals(8, primeraSeccion.getStudentsAtRisk());
        assertEquals(5, primeraSeccion.getActiveAlerts());
    }

    @Test
    void deberiaDevolverElReporteDeProgramacionI() {

        ReportFilter sinFiltros =
                new ReportFilter(null, null, null, null, null, null);

        CourseReportResponse response =
                courseReportService.getCourseReport(4L, sinFiltros);

        assertEquals("Programacion I", response.getCourseName());
        assertEquals(100, response.getStudents());
        assertEquals(3, response.getStudentsAtRisk());
        assertEquals(1, response.getActiveAlerts());
        assertEquals(3, response.getSections().size());
    }

    @Test
    void deberiaLanzar404SiElCursoNoExiste() {

        ReportFilter sinFiltros =
                new ReportFilter(null, null, null, null, null, null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> courseReportService.getCourseReport(999L, sinFiltros));
    }

    @Test
    void deberiaMantenerLaListaDeSeccionesComoLista() {

        ReportFilter sinFiltros =
                new ReportFilter(null, null, null, null, null, null);

        CourseReportResponse response =
                courseReportService.getCourseReport(1L, sinFiltros);

        List<CourseSectionItemResponse> sections =
                response.getSections();

        assertEquals(3, sections.size());
    }
}
