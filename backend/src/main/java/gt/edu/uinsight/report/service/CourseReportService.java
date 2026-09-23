package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.CourseReportResponse;
import gt.edu.uinsight.report.exception.ResourceNotFoundException;
import gt.edu.uinsight.report.mock.MockDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockCourse;
import gt.edu.uinsight.report.mock.model.MockSection;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reporte consolidado por curso (GET /api/v1/reports/courses/{id}).
 */
@Service
public class CourseReportService {

    private final MockDataGateway gateway;
    private final FilterValidator filterValidator;

    public CourseReportService(MockDataGateway gateway, FilterValidator filterValidator) {
        this.gateway = gateway;
        this.filterValidator = filterValidator;
    }

    public CourseReportResponse getCourseReport(Long id, ReportFilter filter) {
        filterValidator.validate(filter);

        MockCourse course = gateway.findCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el curso con id " + id));

        List<MockSection> sections = gateway.findSectionsByCourseId(id);
        int studentsAtRisk = sections.stream()
                .mapToInt(MockSection::getStudentsAtRisk)
                .sum();

        List<MockAlert> courseAlerts = gateway.findAlertsByCourseId(id);
        int activeAlerts = (int) courseAlerts.stream()
                .filter(MockAlert::isActive)
                .count();

        return new CourseReportResponse(
                course.getId(),
                course.getName(),
                course.getTotalStudents(),
                studentsAtRisk,
                activeAlerts);
    }
}
