package gt.edu.uinsight.analytics.summary.dto.request;

import java.util.Objects;

/**
 * Parametro de entrada para la consolidacion de B6.
 *
 * Representa la unica informacion que el cliente envia para pedir el
 * resumen analitico de una seccion:
 *   GET /api/v1/analytics/sections/{id}/summary
 *
 * Aunque el endpoint es un GET y el "id" llega como path variable,
 * este objeto se usa para transportar el sectionId de forma explicita
 * desde el controller hacia el service (SummaryService), en vez de
 * pasar un Long "suelto" por las capas internas.
 */
public class SummaryRequest {

    private Long sectionId;

    public SummaryRequest() {
    }

    public SummaryRequest(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SummaryRequest)) return false;
        SummaryRequest that = (SummaryRequest) o;
        return Objects.equals(sectionId, that.sectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId);
    }

    @Override
    public String toString() {
        return "SummaryRequest{" +
                "sectionId=" + sectionId +
                '}';
    }
}
