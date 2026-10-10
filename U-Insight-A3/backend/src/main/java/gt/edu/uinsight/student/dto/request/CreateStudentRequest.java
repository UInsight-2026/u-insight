package gt.edu.uinsight.student.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos anonimizados para registrar un estudiante")
public record CreateStudentRequest(

        @Schema(description = "Código anónimo único", example = "EST-0001")
        @NotBlank(message = "studentCode es obligatorio")
        @Size(max = 30, message = "studentCode no puede superar 30 caracteres")
        @Pattern(regexp = "^[A-Za-z0-9_-]+$",
                message = "studentCode solo puede contener letras, números, guion y guion bajo")
        String studentCode,

        @Schema(description = "Nombre o alias anonimizado", example = "Estudiante 0001")
        @NotBlank(message = "studentName es obligatorio")
        @Size(max = 120, message = "studentName no puede superar 120 caracteres")
        String studentName,

        @Schema(description = "Correo opcional; no utilizar datos reales de estudiantes",
                example = "estudiante0001@example.test", nullable = true)
        @Email(message = "email debe tener un formato válido")
        @Size(max = 254, message = "email no puede superar 254 caracteres")
        String email
) {
}