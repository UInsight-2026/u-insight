package gt.edu.uinsight.report.dto.response;

import java.util.List;


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
