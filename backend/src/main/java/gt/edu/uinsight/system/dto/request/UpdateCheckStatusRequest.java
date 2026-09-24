package gt.edu.uinsight.system.dto.request;

import gt.edu.uinsight.system.entity.CheckStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCheckStatusRequest(

        @NotNull(message = "status is required")
        CheckStatus status
) {
}
