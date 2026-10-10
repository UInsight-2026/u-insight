package gt.edu.uinsight.student.dto.response;

import gt.edu.uinsight.student.entity.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estudiante registrado")
public record StudentResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "EST-0001") String studentCode,
        @Schema(example = "Estudiante 0001") String studentName,
        @Schema(example = "estudiante0001@example.test", nullable = true) String email,
        @Schema(example = "ACTIVE") StudentStatus status
) {
}