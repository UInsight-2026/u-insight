package gt.edu.uinsight.report.dto.response;

/**
 * Respuesta de GET /api/v1/reports/sections/{id}.
 */
public class SectionReportResponse {

    private final Long sectionId;
    private final String sectionName;
    private final String riskLevel;
    private final int activeAlerts;

    public SectionReportResponse(Long sectionId, String sectionName,
                                  String riskLevel, int activeAlerts) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.riskLevel = riskLevel;
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

    public int getActiveAlerts() {
        return activeAlerts;
    }
}
