package gt.edu.uinsight.enrollment.dto;

import java.time.LocalDate;

public class EnrollmentResponseDTO {

    private Long id;
    private Long sectionId;
    private Long studentId;
    private LocalDate enrollmentDate;
    private String status;

    public EnrollmentResponseDTO() {
    }

    public EnrollmentResponseDTO(Long id, Long sectionId, Long studentId, LocalDate enrollmentDate, String status) {
        this.id = id;
        this.sectionId = sectionId;
        this.studentId = studentId;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}