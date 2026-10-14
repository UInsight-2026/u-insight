package gt.edu.uinsight.analytics.dispersion.dto.response;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Métricas descriptivas de dispersión de las calificaciones")
public record DispersionResponse(
        @Schema(description = "Identificador de la sección cuando la consulta es por sección", example = "10")
        Long sectionId,
        @Schema(description = "Identificador del curso cuando la consulta es por curso", example = "5")
        Long courseId,
        @Schema(description = "Calificación mínima observada", example = "52.00")
        BigDecimal min,
        @Schema(description = "Calificación máxima observada", example = "98.00")
        BigDecimal max,
        @Schema(description = "Diferencia entre la calificación máxima y la mínima", example = "46.00")
        BigDecimal range,
        @Schema(description = "Varianza de las calificaciones", example = "120.50")
        BigDecimal variance,
        @Schema(description = "Desviación estándar de las calificaciones", example = "10.98")
        BigDecimal standardDeviation,
        @Schema(description = "Clasificación de la dispersión", example = "MODERATE_DISPERSION")
        DispersionClassification classification) {
}
