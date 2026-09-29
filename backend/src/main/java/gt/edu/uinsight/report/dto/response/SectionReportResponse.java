package gt.edu.uinsight.report.dto.response;

/**
 * Respuesta de GET /api/v1/reports/sections/{id}.
 *
 * Semana 3: se agregan studentsAtRisk y el bloque analytics,
 * que contiene los indicadores calculados por la Celula B6.
 * Si B6 no responde, analytics llega con available = false
 * y el resto del reporte se entrega igual.
 */
public class SectionReportResponse {

    private final Long sectionId;
    private final String sectionName;
    private final String riskLevel;
    private final int studentsAtRisk;
    private final int activeAlerts;
    private final AnalyticsSnapshot analytics;

    public SectionReportResponse(
            Long sectionId,
            String sectionName,
            String riskLevel,
            int studentsAtRisk,
            int activeAlerts,
            AnalyticsSnapshot analytics) {

        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.riskLevel = riskLevel;
        this.studentsAtRisk = studentsAtRisk;
        this.activeAlerts = activeAlerts;
        this.analytics = analytics;
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

    public AnalyticsSnapshot getAnalytics() {
        return analytics;
    }
}