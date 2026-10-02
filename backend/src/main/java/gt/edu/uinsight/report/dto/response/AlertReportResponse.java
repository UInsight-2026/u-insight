//semana 3
package gt.edu.uinsight.report.dto.response;

import java.util.List;

public class AlertReportResponse {

    private final int total;
    private final int page;
    private final int size;
    private final int totalPages;
    private final List<AlertItemResponse> alerts;

    public AlertReportResponse(int total, int page, int size, int totalPages, List<AlertItemResponse> alerts) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.alerts = alerts;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<AlertItemResponse> getAlerts() {
        return alerts;
    }
}