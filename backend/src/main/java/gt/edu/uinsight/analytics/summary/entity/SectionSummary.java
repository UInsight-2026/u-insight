package gt.edu.uinsight.analytics.summary.entity;

import java.util.List;

import gt.edu.uinsight.analytics.summary.dto.external.CentralTendencyData;
import gt.edu.uinsight.analytics.summary.dto.external.DispersionData;
import gt.edu.uinsight.analytics.summary.dto.external.PositionData;
import gt.edu.uinsight.analytics.summary.dto.external.StudentComparisonData;
import gt.edu.uinsight.analytics.summary.dto.external.TrendData;

public class SectionSummary {
    private Long sectionId;
    private CentralTendencyData centralTendency;
    private PositionData position;
    private DispersionData dispersion;
    private TrendData trend;
    private StudentComparisonData studentComparison;
    private Integer studentsAtRisk;
    private List<String> unavailableComponents;

    public Long getSectionId(){return this.sectionId;}
    public void setSectionId(Long sectionId){this.sectionId = sectionId;}

    public CentralTendencyData getCentralTendencyData(){return this.centralTendency;}
    public void setCentralTendencyData(CentralTendencyData ctd){ this.centralTendency = ctd;}

    public PositionData getPositionData(){return this.position;}
    public void setPositionData(PositionData pd){this.position = pd;}
    
    public DispersionData getPositiDispersionData(){return this.dispersion;}
    public void setDispersionData(DispersionData dd){this.dispersion = dd;}

    public TrendData getTrendData(){return this.trend;}
    public void setTrendData(TrendData td){ this.trend = td;}

    public StudentComparisonData getStudentComparisonData(){return this.studentComparison;}
    public void setStudentComparisonData(StudentComparisonData scd){this.studentComparison = scd;}

    public Integer getStudentsAtRisk(){return this.studentsAtRisk;}
    public void setStudentsAtRisk(Integer sar){this.studentsAtRisk = sar;}

    public List<String> getUnavailableComponents(){return this.unavailableComponents;}
    public void setUnavailableComponents(List<String> unavailable){this.unavailableComponents = unavailable;}
}
