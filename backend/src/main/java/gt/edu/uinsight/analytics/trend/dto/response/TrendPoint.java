package gt.edu.uinsight.analytics.trend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Calificación observada en una evaluación")
public record TrendPoint(
    @Schema(description = "Etiqueta de la evaluación", example = "Parcial 1")
    String label,
    @Schema(description = "Calificación obtenida", example = "85")
    int value
) {
    
}
