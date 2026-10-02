package gt.edu.uinsight.system.dto.response;

import gt.edu.uinsight.system.entity.CheckStatus;

public record IntegrationStatusResponse(
        String name,
        CheckStatus status,
        long responseTimeMs
) {
}