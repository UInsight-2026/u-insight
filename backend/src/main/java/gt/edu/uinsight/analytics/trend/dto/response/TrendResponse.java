package gt.edu.uinsight.analytics.trend.dto.response;

import java.util.List;

public record TrendResponse(
    Long id, 
    String clasification,
    int averageChange,
    List<TrendPoint> points
) {
    
}
