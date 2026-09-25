package gt.edu.uinsight.teacher.dto;

import gt.edu.uinsight.teacher.model.TeacherStatus;
import jakarta.validation.constraints.NotNull;

/** Datos de entrada para el cambio de estado del docente (PATCH). */
public class TeacherStatusRequest {

    @NotNull(message = "El estado es obligatorio. Valores permitidos: ACTIVE, INACTIVE")
    private TeacherStatus status;

    public TeacherStatus getStatus() { return status; }
    public void setStatus(TeacherStatus status) { this.status = status; }
}
