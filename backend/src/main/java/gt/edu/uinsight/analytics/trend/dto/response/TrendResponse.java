package gt.edu.uinsight.analytics.trend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import gt.edu.uinsight.analytics.trend.service.TrendClassification;

public record TrendResponse(
    TrendClassification clasification,
    BigDecimal averageChange,
    List<TrendPoint> points
) {
    
}
