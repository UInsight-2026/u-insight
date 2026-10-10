package gt.edu.uinsight.student.dto.request;

import gt.edu.uinsight.student.entity.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Nuevo estado del estudiante")
public record UpdateStudentStatusRequest(
        @Schema(description = "Estado permitido", example = "INACTIVE")
        @NotNull(message = "status es obligatorio")
        StudentStatus status
) {
}