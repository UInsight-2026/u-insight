package gt.edu.uinsight.report.dto.response;

/**
 * Respuesta de GET /api/v1/reports/overview.
 * Contrato tomado tal cual del diseno de la Semana 1 (Integrante 1 - Angel).
 */
public class OverviewResponse {

    private final int activeAlerts;
    private final int highRiskSections;
    private final int studentsAtRisk;
    private final String overallTrend; // POSITIVE | NEGATIVE | STABLE

    public OverviewResponse(int activeAlerts, int highRiskSections,
                             int studentsAtRisk, String overallTrend) {
        this.activeAlerts = activeAlerts;
        this.highRiskSections = highRiskSections;
        this.studentsAtRisk = studentsAtRisk;
        this.overallTrend = overallTrend;
    }

    public int getActiveAlerts() {
        return activeAlerts;
    }

    public int getHighRiskSections() {
        return highRiskSections;
    }

    public int getStudentsAtRisk() {
        return studentsAtRisk;
    }

    public String getOverallTrend() {
        return overallTrend;
    }
}
