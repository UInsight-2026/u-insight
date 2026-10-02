package gt.edu.uinsight.analytics.trend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import gt.edu.uinsight.analytics.trend.service.TrendClassification;

public record TrendResponse(
    TrendClassification classification,
    BigDecimal averageChange,
    List<TrendPoint> points
) {
    
}
