package gt.edu.uinsight.alert.dto.section;

public class DispersionDto {

    private Double stdDev;

    public DispersionDto() {
    }

    public DispersionDto(Double stdDev) {
        this.stdDev = stdDev;
    }

    public Double getStdDev() {
        return stdDev;
    }

    public void setStdDev(Double stdDev) {
        this.stdDev = stdDev;
    }
}
