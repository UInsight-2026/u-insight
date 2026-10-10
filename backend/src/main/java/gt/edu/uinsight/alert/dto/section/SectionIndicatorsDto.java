package gt.edu.uinsight.alert.dto.section;

public class SectionIndicatorsDto {

    private CentralTendencyDto centralTendency;
    private PositionDto position;
    private DispersionDto dispersion;
    private TrendDto trend;
    private Integer studentsAtRisk;

    public SectionIndicatorsDto() {
    }

    public SectionIndicatorsDto(
            CentralTendencyDto centralTendency,
            PositionDto position,
            DispersionDto dispersion,
            TrendDto trend,
            Integer studentsAtRisk
    ) {
        this.centralTendency = centralTendency;
        this.position = position;
        this.dispersion = dispersion;
        this.trend = trend;
        this.studentsAtRisk = studentsAtRisk;
    }

    public CentralTendencyDto getCentralTendency() {
        return centralTendency;
    }

    public void setCentralTendency(CentralTendencyDto centralTendency) {
        this.centralTendency = centralTendency;
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public DispersionDto getDispersion() {
        return dispersion;
    }

    public void setDispersion(DispersionDto dispersion) {
        this.dispersion = dispersion;
    }

    public TrendDto getTrend() {
        return trend;
    }

    public void setTrend(TrendDto trend) {
        this.trend = trend;
    }

    public Integer getStudentsAtRisk() {
        return studentsAtRisk;
    }

    public void setStudentsAtRisk(Integer studentsAtRisk) {
        this.studentsAtRisk = studentsAtRisk;
    }
}