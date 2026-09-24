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

import java.util.List;

/**
 * Reporte consolidado por curso (GET /api/v1/reports/courses/{id}).
 *
 * Semana 3: agrega el desglose de las secciones del curso,
 * estudiantes en riesgo y alertas activas por sección.
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

        MockCourse course = gateway.findCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el curso con id " + id));

        List<MockSection> sections = gateway.findSectionsByCourseId(id);
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
                        + " activeAlerts=" + activeAlerts);

        return new CourseReportResponse(
                course.getId(),
                course.getName(),
                course.getTotalStudents(),
                studentsAtRisk,
                activeAlerts,
                desglose);
    }

    private int contarAlertasActivasDeLaSeccion(Long sectionId) {

        return (int) gateway.findAlertsBySectionId(sectionId).stream()
                .filter(MockAlert::isActive)
                .count();
    }
}