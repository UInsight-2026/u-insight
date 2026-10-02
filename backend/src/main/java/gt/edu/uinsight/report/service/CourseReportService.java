package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.common.ReportLogger;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.CourseReportResponse;
import gt.edu.uinsight.report.dto.response.CourseSectionItemResponse;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import gt.edu.uinsight.report.exception.ResourceNotFoundException;
import gt.edu.uinsight.report.gateway.ReportDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockCourse;
import gt.edu.uinsight.report.mock.model.MockSection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reporte consolidado por curso (GET /api/v1/reports/courses/{id}).
 *
 * Semana 4: permite entregar una respuesta degradada cuando
 * A1 no publica el catalogo del curso, siempre que existan
 * secciones asociadas al courseId.
 */
@Service
public class CourseReportService {

    private static final String OPERATION = "GET_REPORTS_COURSE";
    private static final String PATH = "/api/v1/reports/courses/{id}";

    private final ReportDataGateway gateway;
    private final FilterValidator filterValidator;
    private final ReportLogger reportLogger;

    public CourseReportService(
            ReportDataGateway gateway,
            FilterValidator filterValidator,
            ReportLogger reportLogger) {

        this.gateway = gateway;
        this.filterValidator = filterValidator;
        this.reportLogger = reportLogger;
    }

    public CourseReportResponse getCourseReport(Long id, ReportFilter filter) {

        long startedAt = System.nanoTime();
        String traceId = reportLogger.start(OPERATION, PATH);

        try {
            filterValidator.validate(filter);
        } catch (InvalidFilterException ex) {
            reportLogger.rejected(traceId, OPERATION, ex.getMessage());
            throw ex;
        }

        Optional<MockCourse> course = gateway.findCourseById(id);
        List<MockSection> sections = gateway.findSectionsByCourseId(id);

        if (course.isEmpty() && sections.isEmpty()) {

            reportLogger.rejected(
                    traceId,
                    OPERATION,
                    "No se encontro el curso con id " + id);

            throw new ResourceNotFoundException(
                    "No se encontro el curso con id " + id);
        }

        List<String> unavailableSources = new ArrayList<>();

        if (course.isEmpty()) {
            unavailableSources.add("A1");
        }

        String courseName = course
                .map(MockCourse::getName)
                .orElse(null);

        /*
         * Cuando A1 esta disponible se utiliza su total.
         *
         * Si A1 no esta disponible, el calculo mediante
         * countEnrolledStudents depende de la integracion
         * pendiente del ReportDataGateway.
         */
        int totalStudents = course
                .map(MockCourse::getTotalStudents)
                .orElse(0);

        List<MockAlert> courseAlerts = gateway.findAlertsByCourseId(id);

        int studentsAtRisk = sections.stream()
                .mapToInt(MockSection::getStudentsAtRisk)
                .sum();

        int activeAlerts = (int) courseAlerts.stream()
                .filter(MockAlert::isActive)
                .count();

        List<CourseSectionItemResponse> desglose = sections.stream()
                .map(section -> new CourseSectionItemResponse(
                        section.getId(),
                        section.getName(),
                        section.getRiskLevel(),
                        section.getStudentsAtRisk(),
                        contarAlertasActivasDeLaSeccion(section.getId())))
                .toList();

        reportLogger.success(
                traceId,
                OPERATION,
                startedAt,
                "courseId=" + id
                        + " sections=" + desglose.size()
                        + " studentsAtRisk=" + studentsAtRisk
                        + " activeAlerts=" + activeAlerts
                        + " unavailableSources=" + unavailableSources);

        return new CourseReportResponse(
                id,
                courseName,
                totalStudents,
                studentsAtRisk,
                activeAlerts,
                desglose,
                unavailableSources);
    }

    private int contarAlertasActivasDeLaSeccion(Long sectionId) {

        return (int) gateway.findAlertsBySectionId(sectionId).stream()
                .filter(MockAlert::isActive)
                .count();
    }
}