package gt.edu.uinsight.analytics.trend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import gt.edu.uinsight.analytics.trend.service.TrendClassification;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Clasificación y serie histórica de la evolución de calificaciones")
public record TrendResponse(
    @Schema(description = "Sentido de la tendencia; INSUFFICIENT_DATA indica que no hay evaluaciones suficientes",
        example = "POSITIVE")
    TrendClassification classification,
    @Schema(description = "Cambio promedio entre evaluaciones; puede ser nulo si no hay datos suficientes",
        example = "3.50")
    BigDecimal averageChange,
    @Schema(description = "Puntos de calificación ordenados por evaluación")
    List<TrendPoint> points
) {
    
}
