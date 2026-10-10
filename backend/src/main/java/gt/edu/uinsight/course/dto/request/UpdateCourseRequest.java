package gt.edu.uinsight.course.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos editables de un curso. El codigo no cambia (otras celulas lo usan como
 * referencia) y el estado solo cambia con PATCH /{id}/status.
 */
public record UpdateCourseRequest(
        @Schema(example = "Programacion II") @NotBlank @Size(max = 150) String name,
        @Schema(example = "POO, colecciones y APIs REST") @Size(max = 255) String description,
        @Schema(example = "5") Integer credits
) {
}
