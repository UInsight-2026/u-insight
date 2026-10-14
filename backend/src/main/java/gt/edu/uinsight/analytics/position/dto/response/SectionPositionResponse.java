package gt.edu.uinsight.analytics.position.dto.response;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta de GET /api/v1/analytics/sections/{id}/position
 *
 * quartiles siempre incluye Q1, Q2 y Q3.
 * percentiles solo se llena si el cliente pidio ?percentiles=25,50,75,90
 */
@Schema(description = "Medidas de posición de las calificaciones de una sección")
public class SectionPositionResponse {

    @Schema(description = "Identificador de la sección", example = "10")
    private Long sectionId;
    @Schema(description = "Cantidad de calificaciones consideradas", example = "24")
    private int sampleSize;
    @Schema(description = "Cuartiles Q1, Q2 y Q3",
            example = "{\"Q1\": 65.0, \"Q2\": 78.5, \"Q3\": 89.0}")
    private Map<String, Double> quartiles;
    @Schema(description = "Percentiles adicionales solicitados (1 a 99); vacío si no se solicitó ninguno",
            example = "{\"P25\": 65.0, \"P90\": 94.0}")
    private Map<String, Double> percentiles;

    public SectionPositionResponse(Long sectionId, int sampleSize, Map<String, Double> quartiles, Map<String, Double> percentiles) {
        this.sectionId = sectionId;
        this.sampleSize = sampleSize;
        this.quartiles = quartiles;
        this.percentiles = percentiles;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public Map<String, Double> getQuartiles() {
        return quartiles;
    }

    public Map<String, Double> getPercentiles() {
        return percentiles;
    }
}
