package gt.edu.uinsight.section.dto;

public class SectionResponseDTO {

    private Long id;
    private Long academicPeriodId;
    private Long courseId;
    private Long teacherId;
    private String sectionCode;
    private String status;

    public SectionResponseDTO() {
    }

    public SectionResponseDTO(Long id, Long academicPeriodId, Long courseId, Long teacherId, String sectionCode, String status) {
        this.id = id;
        this.academicPeriodId = academicPeriodId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.sectionCode = sectionCode;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAcademicPeriodId() { return academicPeriodId; }
    public void setAcademicPeriodId(Long academicPeriodId) { this.academicPeriodId = academicPeriodId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}