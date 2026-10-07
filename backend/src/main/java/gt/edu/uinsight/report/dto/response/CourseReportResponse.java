package gt.edu.uinsight.report.dto.response;

import java.util.List;

/**
 * Respuesta de GET /api/v1/reports/courses/{id}.
 *
 * Semana 4: permite entregar el reporte aun cuando alguna
 * fuente externa no se encuentre disponible.
 */
public class CourseReportResponse {

    private final Long courseId;
    private final String courseName;
    private final int students;
    private final int studentsAtRisk;
    private final int activeAlerts;
    private final List<CourseSectionItemResponse> sections;
    private final List<String> unavailableSources;

    public CourseReportResponse(
            Long courseId,
            String courseName,
            int students,
            int studentsAtRisk,
            int activeAlerts,
            List<CourseSectionItemResponse> sections,
            List<String> unavailableSources) {

        this.courseId = courseId;
        this.courseName = courseName;
        this.students = students;
        this.studentsAtRisk = studentsAtRisk;
        this.activeAlerts = activeAlerts;
        this.sections = sections;
        this.unavailableSources = unavailableSources;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getStudents() {
        return students;
    }

    public int getStudentsAtRisk() {
        return studentsAtRisk;
    }

    public int getActiveAlerts() {
        return activeAlerts;
    }

    public List<CourseSectionItemResponse> getSections() {
        return sections;
    }

    public List<String> getUnavailableSources() {
        return unavailableSources;
    }
}