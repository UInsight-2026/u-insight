package gt.edu.uinsight.indicatorconfiguration.dto.response;

import java.time.LocalDateTime;

public record IndicatorConfigurationResponse(
        Long id,
        String key,
        String value,
        String description,
        LocalDateTime updatedAt
) {
}
