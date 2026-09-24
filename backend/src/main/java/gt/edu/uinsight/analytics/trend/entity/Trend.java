package gt.edu.uinsight.analytics.trend.entity;

import java.math.BigDecimal;
import java.util.List;

import gt.edu.uinsight.analytics.trend.dto.response.TrendPoint;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;

/**
 * Resultado calculado al vuelo. El modelo oficial no define una tabla de
 * tendencias, por lo que esta clase no es una entidad JPA.
 */
public record Trend(
        TrendClassification classification,
        BigDecimal averageChange,
        List<TrendPoint> points) {
}
