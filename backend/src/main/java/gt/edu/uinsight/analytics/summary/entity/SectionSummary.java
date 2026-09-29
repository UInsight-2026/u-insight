package gt.edu.uinsight.analytics.summary.entity;

import java.util.List;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.summary.dto.external.PositionData;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;

public class SectionSummary {

    private Long sectionId;
    private CentralTendencyResponse centralTendency;
    private PositionData position;
    private DispersionResponse dispersion;
    private TrendResponse trend;
    private StudentComparisonResponse studentComparison;
    private Integer studentsAtRisk;
    private List<String> unavailableComponents;

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public CentralTendencyResponse getCentralTendencyData() { return centralTendency; }
    public void setCentralTendencyData(CentralTendencyResponse ct) { this.centralTendency = ct; }

    public PositionData getPositionData() { return position; }
    public void setPositionData(PositionData pd) { this.position = pd; }

    public DispersionResponse getDispersionData() { return dispersion; }
    public void setDispersionData(DispersionResponse dd) { this.dispersion = dd; }

    public TrendResponse getTrendData() { return trend; }
    public void setTrendData(TrendResponse td) { this.trend = td; }

    public StudentComparisonResponse getStudentComparisonData() { return studentComparison; }
    public void setStudentComparisonData(StudentComparisonResponse scd) { this.studentComparison = scd; }

    public Integer getStudentsAtRisk() { return studentsAtRisk; }
    public void setStudentsAtRisk(Integer sar) { this.studentsAtRisk = sar; }

    public List<String> getUnavailableComponents() { return unavailableComponents; }
    public void setUnavailableComponents(List<String> unavailable) { this.unavailableComponents = unavailable; }
}