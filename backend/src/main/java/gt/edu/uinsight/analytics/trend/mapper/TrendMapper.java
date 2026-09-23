package gt.edu.uinsight.analytics.trend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendCalculator;

@Component 
public class TrendMapper {

    public  TrendResponse toTrendResponse(TrendCalculator.Result result, List<TrendCalculator.ScorePoint> points) {
        return new TrendResponse(result.classification(), result.averageChange(), points.stream()
                .map(p -> new gt.edu.uinsight.analytics.trend.dto.response.TrendPoint(
                        p.label(),
                        p.normalizedValue().intValue()))
                .toList());
    }
}
