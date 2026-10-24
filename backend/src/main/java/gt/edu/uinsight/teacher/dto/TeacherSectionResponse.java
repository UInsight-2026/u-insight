package gt.edu.uinsight.teacher.dto;

import gt.edu.uinsight.analytics.trend.entity.Section;

/** Representacion de salida de una seccion asignada a un docente. */
public class TeacherSectionResponse {

    private final Long id;
    private final String sectionCode;
    private final String status;
    private final Long periodId;
    private final Long courseId;
    private final Long teacherId;

    private TeacherSectionResponse(Section section) {
        this.id = section.getId();
        this.sectionCode = section.getSectionCode();
        this.status = section.getStatus();
        this.periodId = section.getPeriodId();
        this.courseId = section.getCourseId();
        this.teacherId = section.getTeacherId();
    }

    public static TeacherSectionResponse from(Section section) {
        return new TeacherSectionResponse(section);
    }

    public Long getId() { return id; }

    public String getSectionCode() { return sectionCode; }

    public String getStatus() { return status; }

    public Long getPeriodId() { return periodId; }

    public Long getCourseId() { return courseId; }

    public Long getTeacherId() { return teacherId; }
}
