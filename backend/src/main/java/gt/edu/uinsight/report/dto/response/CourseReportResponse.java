package gt.edu.uinsight.report.dto.response;

import java.util.List;

/**
 * Respuesta de GET /api/v1/reports/courses/{id}.
 *
 * Semana 3: se agrega el desglose de las secciones
 * pertenecientes al curso.
 */
public class CourseReportResponse {

    private final Long courseId;
    private final String courseName;
    private final int students;
    private final int studentsAtRisk;
    private final int activeAlerts;
    private final List<CourseSectionItemResponse> sections;

    public CourseReportResponse(
            Long courseId,
            String courseName,
            int students,
            int studentsAtRisk,
            int activeAlerts,
            List<CourseSectionItemResponse> sections) {

        this.courseId = courseId;
        this.courseName = courseName;
        this.students = students;
        this.studentsAtRisk = studentsAtRisk;
        this.activeAlerts = activeAlerts;
        this.sections = sections;
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
}