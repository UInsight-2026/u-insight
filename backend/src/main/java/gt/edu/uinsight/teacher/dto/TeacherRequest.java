package gt.edu.uinsight.teacher.dto;

import gt.edu.uinsight.teacher.model.TeacherStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de entrada para crear (POST) o actualizar (PUT) un docente. */
public class TeacherRequest {

    @NotBlank(message = "El codigo del docente es obligatorio")
    @Size(max = 20, message = "El codigo del docente no puede exceder 20 caracteres")
    private String teacherCode;

    @NotBlank(message = "El nombre del docente es obligatorio")
    @Size(max = 120, message = "El nombre del docente no puede exceder 120 caracteres")
    private String teacherName;

    @Email(message = "El correo electronico no tiene un formato valido")
    @Size(max = 150, message = "El correo electronico no puede exceder 150 caracteres")
    private String email;

    private TeacherStatus status;

    public String getTeacherCode() { return teacherCode; }
    public void setTeacherCode(String teacherCode) { this.teacherCode = teacherCode; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public TeacherStatus getStatus() { return status; }
    public void setStatus(TeacherStatus status) { this.status = status; }
}
