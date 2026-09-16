package gt.edu.uinsight.analytics.individual.dto.response;

import java.math.BigDecimal;

public record StudentSummaryResponse(
        String studentCode,
        BigDecimal studentAverage,
        BigDecimal sectionAverage,
        BigDecimal difference
) {
}
