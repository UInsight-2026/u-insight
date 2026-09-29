package gt.edu.uinsight.report.dto.response;

import java.util.List;

/**
 * Respuesta de GET /api/v1/reports/overview.
 *
 * Semana 3: se agregan trendSource y unavailableSources para que el
 * consumidor (C6) sepa de donde salio la tendencia y si algun origen de
 * datos no estuvo disponible. Los cuatro campos originales no cambian.
 */
public class OverviewResponse {

    private final int activeAlerts;
    private final int highRiskSections;
    private final int studentsAtRisk;
    private final String overallTrend;        // POSITIVE | NEGATIVE | STABLE | INSUFFICIENT_DATA
    private final String trendSource;         // B6 | LOCAL_FALLBACK | NONE
    private final List<String> unavailableSources;

    public OverviewResponse(int activeAlerts, int highRiskSections, int studentsAtRisk,
                            String overallTrend, String trendSource,
                            List<String> unavailableSources) {
        this.activeAlerts = activeAlerts;
        this.highRiskSections = highRiskSections;
        this.studentsAtRisk = studentsAtRisk;
        this.overallTrend = overallTrend;
        this.trendSource = trendSource;
        this.unavailableSources = unavailableSources;
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

    public String getTrendSource() {
        return trendSource;
    }

    public List<String> getUnavailableSources() {
        return unavailableSources;
    }
}
