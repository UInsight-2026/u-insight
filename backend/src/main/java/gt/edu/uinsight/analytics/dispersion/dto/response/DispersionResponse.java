
package gt.edu.uinsight.analytics.dispersion.dto.response;

import java.math.BigDecimal;

public record DispersionResponse(
        Long sectionId,
        Long courseId,
        BigDecimal min,
        BigDecimal max,
        BigDecimal range,
        BigDecimal variance,
        BigDecimal standardDeviation,
        DispersionClassification classification) {
}
