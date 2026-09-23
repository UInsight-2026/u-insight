package gt.edu.uinsight.system.dto.request;

import gt.edu.uinsight.system.entity.CheckStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCheckRequest(

        @NotBlank(message = "component is required")
        @Size(max = 100, message = "component must be at most 100 characters")
        String component,

        @NotNull(message = "status is required")
        CheckStatus status,

        @Size(max = 500, message = "message must be at most 500 characters")
        String message
) {
}