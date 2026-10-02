package gt.edu.uinsight.analytics.position.dto.response;

import java.util.Map;

/**
 * Respuesta de GET /api/v1/analytics/sections/{id}/position
 *
 * quartiles siempre incluye Q1, Q2 y Q3.
 * percentiles solo se llena si el cliente pidio ?percentiles=25,50,75,90
 */
public class SectionPositionResponse {

    private Long sectionId;
    private int sampleSize;
    private Map<String, Double> quartiles;
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
