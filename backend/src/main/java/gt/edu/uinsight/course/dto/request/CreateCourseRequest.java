package gt.edu.uinsight.course.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Los creditos son opcionales; si se envian, RN-08 exige que sean mayores que cero
 * (se valida en el servicio).
 */
public record CreateCourseRequest(
        @Schema(example = "PROG-II") @NotBlank @Size(max = 20) String code,
        @Schema(example = "Programacion II") @NotBlank @Size(max = 150) String name,
        @Schema(example = "Curso de POO y APIs REST") @Size(max = 255) String description,
        @Schema(example = "5") Integer credits
) {
}
