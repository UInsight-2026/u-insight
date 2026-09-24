package gt.edu.uinsight.section.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos de entrada para registrar una seccion y asignarle un docente. */
public class SectionRequest {

    @NotBlank(message = "El codigo de la seccion es obligatorio")
    @Size(max = 20, message = "El codigo de la seccion no puede exceder 20 caracteres")
    private String sectionCode;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 120, message = "El nombre del curso no puede exceder 120 caracteres")
    private String courseName;

    @NotBlank(message = "El ciclo academico es obligatorio")
    @Size(max = 20, message = "El ciclo academico no puede exceder 20 caracteres")
    private String academicTerm;

    @NotNull(message = "El identificador del docente es obligatorio")
    private Long teacherId;

    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getAcademicTerm() { return academicTerm; }
    public void setAcademicTerm(String academicTerm) { this.academicTerm = academicTerm; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}
