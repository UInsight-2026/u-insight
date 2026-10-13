package gt.edu.uinsight.analytics.dispersion.dto.request;

import java.util.List;

public record DispersionRequest(
        Long sectionId,
        List<Long> studentIds) {
}
