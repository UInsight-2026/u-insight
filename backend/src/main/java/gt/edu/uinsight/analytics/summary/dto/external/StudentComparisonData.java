package gt.edu.uinsight.analytics.summary.dto.external;

public class StudentComparisonData {
     private String studentCode;
    private Double studentAverage;
    private Double sectionAverage;
    private Double difference;
    private Double percentile;
    private String trend; // POSITIVE, NEGATIVE, STABLE, INSUFFICIENT_DATA

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public Double getStudentAverage() { return studentAverage; }
    public void setStudentAverage(Double studentAverage) { this.studentAverage = studentAverage; }

    public Double getSectionAverage() { return sectionAverage; }
    public void setSectionAverage(Double sectionAverage) { this.sectionAverage = sectionAverage; }

    public Double getDifference() { return difference; }
    public void setDifference(Double difference) { this.difference = difference; }

    public Double getPercentile() { return percentile; }
    public void setPercentile(Double percentile) { this.percentile = percentile; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
}
