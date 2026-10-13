package gt.edu.uinsight.analytics.individual.dto.response;

import java.math.BigDecimal;

public record StudentComparisonResponse(
        String studentCode,
        BigDecimal studentAverage,
        BigDecimal sectionAverage,
        BigDecimal difference,
        Integer percentile
) {
}