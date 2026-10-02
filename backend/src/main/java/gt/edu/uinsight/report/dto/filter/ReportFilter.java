package gt.edu.uinsight.report.dto.filter;

public class ReportFilter {

    private final String period;
    private final String course;
    private final String teacher;
    private final String section;
    private final String riskLevel;
    private final String alertStatus;

    public ReportFilter(String period, String course, String teacher,
                         String section, String riskLevel, String alertStatus) {
        this.period = period;
        this.course = course;
        this.teacher = teacher;
        this.section = section;
        this.riskLevel = riskLevel;
        this.alertStatus = alertStatus;
    }

    public String getPeriod() {
        return period;
    }

    public String getCourse() {
        return course;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getSection() {
        return section;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getAlertStatus() {
        return alertStatus;
    }
}
