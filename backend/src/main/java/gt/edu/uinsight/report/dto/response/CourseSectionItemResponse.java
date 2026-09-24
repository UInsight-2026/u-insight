package gt.edu.uinsight.report.dto.response;

public class CourseSectionItemResponse {

    private final Long sectionId;
    private final String sectionName;
    private final String riskLevel;
    private final int studentsAtRisk;
    private final int activeAlerts;

    public CourseSectionItemResponse(
            Long sectionId,
            String sectionName,
            String riskLevel,
            int studentsAtRisk,
            int activeAlerts) {

        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.riskLevel = riskLevel;
        this.studentsAtRisk = studentsAtRisk;
        this.activeAlerts = activeAlerts;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public int getStudentsAtRisk() {
        return studentsAtRisk;
    }

    public int getActiveAlerts() {
        return activeAlerts;
    }
}