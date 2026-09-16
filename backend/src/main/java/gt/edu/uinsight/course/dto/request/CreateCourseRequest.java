package gt.edu.uinsight.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCourseRequest(
        @NotBlank @Size(max = 20) String code,
        @NotBlank @Size(max = 150) String name,
        @Size(max = 500) String description,
        Integer credits
) {
}
