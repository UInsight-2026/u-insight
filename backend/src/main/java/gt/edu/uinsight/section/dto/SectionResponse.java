package gt.edu.uinsight.section.dto;

import gt.edu.uinsight.section.model.Section;

/** Representacion de salida de una seccion. */
public class SectionResponse {

    private final Long id;
    private final String sectionCode;
    private final String courseName;
    private final String academicTerm;
    private final Long teacherId;
    private final String teacherName;

    private SectionResponse(Section section) {
        this.id = section.getId();
        this.sectionCode = section.getSectionCode();
        this.courseName = section.getCourseName();
        this.academicTerm = section.getAcademicTerm();
        this.teacherId = section.getTeacher().getId();
        this.teacherName = section.getTeacher().getTeacherName();
    }

    public static SectionResponse from(Section section) {
        return new SectionResponse(section);
    }

    public Long getId() { return id; }

    public String getSectionCode() { return sectionCode; }

    public String getCourseName() { return courseName; }

    public String getAcademicTerm() { return academicTerm; }

    public Long getTeacherId() { return teacherId; }

    public String getTeacherName() { return teacherName; }
}
