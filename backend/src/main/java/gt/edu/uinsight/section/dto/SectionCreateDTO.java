package gt.edu.uinsight.section.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SectionCreateDTO {

    @NotNull(message = "El periodo académico es obligatorio")
    private Long academicPeriodId;

    @NotNull(message = "El curso es obligatorio")
    private Long courseId;

    @NotNull(message = "El docente es obligatorio")
    private Long teacherId;

    @NotBlank(message = "El código de sección es obligatorio")
    private String sectionCode;

    public SectionCreateDTO() {
    }

    public SectionCreateDTO(Long academicPeriodId, Long courseId, Long teacherId, String sectionCode) {
        this.academicPeriodId = academicPeriodId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.sectionCode = sectionCode;
    }

    public Long getAcademicPeriodId() { return academicPeriodId; }
    public void setAcademicPeriodId(Long academicPeriodId) { this.academicPeriodId = academicPeriodId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }
}