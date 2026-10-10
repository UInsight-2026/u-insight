package gt.edu.uinsight.alert.dto.section;

public class CentralTendencyDto {

    private Double mean;
    private Double median;

    public CentralTendencyDto() {
    }

    public CentralTendencyDto(Double mean, Double median) {
        this.mean = mean;
        this.median = median;
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }
}
