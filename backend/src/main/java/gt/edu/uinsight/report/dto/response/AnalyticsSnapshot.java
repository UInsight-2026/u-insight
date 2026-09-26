package gt.edu.uinsight.report.dto.response;

/** Datos de B6 en el contrato de C5. Los componentes ausentes permanecen null. */
public class AnalyticsSnapshot {
    private final Long sectionId;
    private final Double mean;
    private final Double median;
    private final Integer sampleSize;
    private final Double standardDeviation;
    private final String trendClassification;
    private final Double averageChange;
    private final boolean available;

    public AnalyticsSnapshot(Long sectionId, Double mean, Double median, Integer sampleSize,
                             Double standardDeviation, String trendClassification,
                             Double averageChange, boolean available) {
        this.sectionId = sectionId;
        this.mean = mean;
        this.median = median;
        this.sampleSize = sampleSize;
        this.standardDeviation = standardDeviation;
        this.trendClassification = trendClassification;
        this.averageChange = averageChange;
        this.available = available;
    }

    public static AnalyticsSnapshot unavailable(Long sectionId) {
        return new AnalyticsSnapshot(sectionId, null, null, null, null, null, null, false);
    }

    public Long getSectionId() { return sectionId; }
    public Double getMean() { return mean; }
    public Double getMedian() { return median; }
    public Integer getSampleSize() { return sampleSize; }
    public Double getStandardDeviation() { return standardDeviation; }
    public String getTrendClassification() { return trendClassification; }
    public Double getAverageChange() { return averageChange; }
    public boolean isAvailable() { return available; }
}
