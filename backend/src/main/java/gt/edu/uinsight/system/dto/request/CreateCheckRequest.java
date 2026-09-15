package gt.edu.uinsight.system.dto.request;

import gt.edu.uinsight.system.entity.CheckStatus;

public record CreateCheckRequest(
        String component,
        CheckStatus status,
        String message
) {
}
