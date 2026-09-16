package gt.edu.uinsight.report.dto.response;

import java.util.List;

/**
 * Respuesta de GET /api/v1/reports/alerts.
 * El documento del proyecto no fijo un ejemplo exacto de JSON para este
 * endpoint (solo lo describe como "listado consolidado de alertas"), asi
 * que el equipo definio esta forma: total + lista de alertas.
 * (Integrante 3 - Allan).
 */
public class AlertReportResponse {

    private final int total;
    private final List<AlertItemResponse> alerts;

    public AlertReportResponse(int total, List<AlertItemResponse> alerts) {
        this.total = total;
        this.alerts = alerts;
    }

    public int getTotal() {
        return total;
    }

    public List<AlertItemResponse> getAlerts() {
        return alerts;
    }
}
