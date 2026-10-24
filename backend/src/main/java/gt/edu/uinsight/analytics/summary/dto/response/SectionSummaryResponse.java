package gt.edu.uinsight.analytics.summary.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Objeto central de la respuesta de B6 - Consolidacion Analitica.
 *
 * Une en una sola respuesta los resultados de B1 (tendencia central),
 * B2 (posicion), B3 (dispersion) y B4 (evolucion/tendencia), mas el
 * conteo de estudiantes en riesgo y la lista de componentes que
 * fallaron en caso de error parcial (HTTP 206).
 *
 * Notas de implementacion:
 * - centralTendency, position, dispersion y trend se dejan como Object
 *   porque B1-B4 todavia no exponen sus DTO de respuesta (sus paquetes
 *   solo tienen ".gitkeep"). En cuanto esas celulas publiquen sus
 *   clases (p. ej. CentralTendencyResponse, PositionResponse,
 *   DispersionResponse, TrendResponse), estos campos deben tiparse con
 *   esas clases concretas en lugar de Object.
 * - Si un componente no esta disponible (timeout/fallo) o hay datos
 *   insuficientes, el campo correspondiente se
 *   deja en null y su nombre se agrega a unavailableComponents.
 *
 * IMPORTANTE - contrato real de B1-B4 (segun sus documentos de diseno,
 * mas detallado que el ejemplo simplificado del documento de B6):
 * - centralTendency (B1 / CentralTendencyResponse): sampleSize (int),
 *   mean (Double), median (Double), mode (List<Double>).
 * - position (B2 / PositionResponse): sectionId, sampleSize,
 *   percentiles (Map<String, Double>), quartiles ({q1, q2, q3}).
 * - dispersion (B3 / DispersionResponse): sectionId, min, max, range,
 *   variance, standardDeviation, classification (LOW_DISPERSION /
 *   MODERATE_DISPERSION / HIGH_DISPERSION). B3 puede responder 422
 *   cuando no hay datos suficientes (no solo timeout/404).
 * - trend (B4 / TrendResponse): scope, referenceId, classification
 *   (POSITIVE/NEGATIVE/STABLE/INSUFFICIENT_DATA), averageChange
 *   (BigDecimal, null si INSUFFICIENT_DATA), points (List<TrendPoint>
 *   con evaluationId, label, evaluationDate, value, present) y
 *   warnings (List<String>). classification = INSUFFICIENT_DATA es
 *   una respuesta 200 valida, NO un fallo: no debe marcarse "trend"
 *   en unavailableComponents solo por tener datos insuficientes.
 */
public class SectionSummaryResponse {

    private Long sectionId;
    private Object centralTendency;   // Resultado de B1 (media, mediana, moda)
    private Object position;          // Resultado de B2 (cuartiles, percentiles)
    private Object dispersion;        // Resultado de B3 (min, max, rango, varianza, desviacion)
    private Object trend;             // Resultado de B4 (clasificacion y puntos de evolucion)
    private Integer studentsAtRisk;
    private List<String> unavailableComponents = new ArrayList<>();

    public SectionSummaryResponse() {
    }

    public SectionSummaryResponse(Long sectionId,
                                   Object centralTendency,
                                   Object position,
                                   Object dispersion,
                                   Object trend,
                                   Integer studentsAtRisk,
                                   List<String> unavailableComponents) {
        this.sectionId = sectionId;
        this.centralTendency = centralTendency;
        this.position = position;
        this.dispersion = dispersion;
        this.trend = trend;
        this.studentsAtRisk = studentsAtRisk;
        this.unavailableComponents = unavailableComponents != null
                ? unavailableComponents
                : new ArrayList<>();
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Object getCentralTendency() {
        return centralTendency;
    }

    public void setCentralTendency(Object centralTendency) {
        this.centralTendency = centralTendency;
    }

    public Object getPosition() {
        return position;
    }

    public void setPosition(Object position) {
        this.position = position;
    }

    public Object getDispersion() {
        return dispersion;
    }

    public void setDispersion(Object dispersion) {
        this.dispersion = dispersion;
    }

    public Object getTrend() {
        return trend;
    }

    public void setTrend(Object trend) {
        this.trend = trend;
    }

    public Integer getStudentsAtRisk() {
        return studentsAtRisk;
    }

    public void setStudentsAtRisk(Integer studentsAtRisk) {
        this.studentsAtRisk = studentsAtRisk;
    }

    public List<String> getUnavailableComponents() {
        return unavailableComponents;
    }

    public void setUnavailableComponents(List<String> unavailableComponents) {
        this.unavailableComponents = unavailableComponents != null
                ? unavailableComponents
                : new ArrayList<>();
    }

    /**
     * Utilidad para que SummaryService marque un componente como no
     * disponible sin tener que manipular la lista directamente.
     */
    public void markUnavailable(String componentName) {
        if (componentName != null && !this.unavailableComponents.contains(componentName)) {
            this.unavailableComponents.add(componentName);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectionSummaryResponse)) return false;
        SectionSummaryResponse that = (SectionSummaryResponse) o;
        return Objects.equals(sectionId, that.sectionId)
                && Objects.equals(centralTendency, that.centralTendency)
                && Objects.equals(position, that.position)
                && Objects.equals(dispersion, that.dispersion)
                && Objects.equals(trend, that.trend)
                && Objects.equals(studentsAtRisk, that.studentsAtRisk)
                && Objects.equals(unavailableComponents, that.unavailableComponents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId, centralTendency, position, dispersion,
                trend, studentsAtRisk, unavailableComponents);
    }

    @Override
    public String toString() {
        return "SectionSummaryResponse{" +
                "sectionId=" + sectionId +
                ", centralTendency=" + centralTendency +
                ", position=" + position +
                ", dispersion=" + dispersion +
                ", trend=" + trend +
                ", studentsAtRisk=" + studentsAtRisk +
                ", unavailableComponents=" + unavailableComponents +
                '}';
    }
}
