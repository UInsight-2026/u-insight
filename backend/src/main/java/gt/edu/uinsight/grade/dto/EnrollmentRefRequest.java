package gt.edu.uinsight.grade.dto;

public class EnrollmentRefRequest {

    private Long studentId;
    private Long sectionId;

    public EnrollmentRefRequest() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
}
