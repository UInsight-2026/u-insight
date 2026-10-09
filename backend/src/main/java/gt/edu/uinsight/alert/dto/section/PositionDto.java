package gt.edu.uinsight.alert.dto.section;

public class PositionDto {

    private Double percentile90;

    public PositionDto() {
    }

    public PositionDto(Double percentile90) {
        this.percentile90 = percentile90;
    }

    public Double getPercentile90() {
        return percentile90;
    }

    public void setPercentile90(Double percentile90) {
        this.percentile90 = percentile90;
    }
}
