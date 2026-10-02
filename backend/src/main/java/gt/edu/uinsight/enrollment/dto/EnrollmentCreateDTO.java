package gt.edu.uinsight.enrollment.dto;

import jakarta.validation.constraints.NotNull;

public class EnrollmentCreateDTO {

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;

    public EnrollmentCreateDTO() {
    }

    public EnrollmentCreateDTO(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
}