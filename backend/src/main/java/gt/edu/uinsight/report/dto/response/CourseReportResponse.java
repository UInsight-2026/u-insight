package gt.edu.uinsight.report.dto.response;


public class CourseReportResponse {

    private final Long courseId;
    private final String courseName;
    private final int students;
    private final int studentsAtRisk;
    private final int activeAlerts;

    public CourseReportResponse(Long courseId, String courseName, int students,
                                 int studentsAtRisk, int activeAlerts) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.students = students;
        this.studentsAtRisk = studentsAtRisk;
        this.activeAlerts = activeAlerts;
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
}
