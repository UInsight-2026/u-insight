package gt.edu.uinsight.system.dto.response;

import gt.edu.uinsight.system.entity.CheckStatus;

import java.time.LocalDateTime;

public record CheckResponse(
        Long id,
        String component,
        CheckStatus status,
        String message,
        LocalDateTime checkedAt
) {
}
