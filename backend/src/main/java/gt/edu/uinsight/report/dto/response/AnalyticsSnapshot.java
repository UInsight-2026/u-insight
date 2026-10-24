package gt.edu.uinsight.report.dto.response;

/** Datos de B6 en el contrato de C5. Los componentes ausentes permanecen null. */
public class AnalyticsSnapshot {

    /** No hay dispersion que publicar. */
    public static final String DISPERSION_NO_DISPONIBLE = "NONE";

    private final Long sectionId;
    private final Double mean;
    private final Double median;
    private final Integer sampleSize;
    private final Double standardDeviation;
    private final String trendClassification;
    private final Double averageChange;
    /**
     * Origen del bloque de dispersion:
     *   B3        el valor viene calculado a partir de las notas de la seccion
     *   B3_FIXED  el valor llega constante desde el origen, no calculado
     *   NONE      no hay dispersion disponible
     *
     * La media y la mediana si son calculadas. Sin esta marca, quien lea la
     * respuesta no tiene forma de distinguir un dato calculado de uno fijo.
     */
    private final String dispersionSource;
    private final boolean available;
    /** Null cuando B6 no pudo calcularlo para esta seccion (no es lo mismo que 0 reales). */
    private final Integer studentsAtRisk;

    public AnalyticsSnapshot(Long sectionId, Double mean, Double median, Integer sampleSize,
                             Double standardDeviation, String trendClassification,
                             Double averageChange, String dispersionSource, boolean available) {
        this(sectionId, mean, median, sampleSize, standardDeviation, trendClassification,
                averageChange, dispersionSource, available, null);
    }

    public AnalyticsSnapshot(Long sectionId, Double mean, Double median, Integer sampleSize,
                             Double standardDeviation, String trendClassification,
                             Double averageChange, String dispersionSource, boolean available,
                             Integer studentsAtRisk) {
        this.sectionId = sectionId;
        this.mean = mean;
        this.median = median;
        this.sampleSize = sampleSize;
        this.standardDeviation = standardDeviation;
        this.trendClassification = trendClassification;
        this.averageChange = averageChange;
        this.dispersionSource = dispersionSource;
        this.available = available;
        this.studentsAtRisk = studentsAtRisk;
    }

    public static AnalyticsSnapshot unavailable(Long sectionId) {
        return new AnalyticsSnapshot(sectionId, null, null, null, null, null, null,
                DISPERSION_NO_DISPONIBLE, false);
    }

    public Long getSectionId() { return sectionId; }
    public Double getMean() { return mean; }
    public Double getMedian() { return median; }
    public Integer getSampleSize() { return sampleSize; }
    public Double getStandardDeviation() { return standardDeviation; }
    public String getTrendClassification() { return trendClassification; }
    public Double getAverageChange() { return averageChange; }
    public String getDispersionSource() { return dispersionSource; }
    public boolean isAvailable() { return available; }
    public Integer getStudentsAtRisk() { return studentsAtRisk; }
}
