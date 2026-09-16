package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AlertItemResponse;
import gt.edu.uinsight.report.dto.response.AlertReportResponse;
import gt.edu.uinsight.report.mock.MockDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Reporte consolidado de alertas (GET /reports/alerts).
 * Integrante 3 - Allan (Alertas + filtros).
 */
@Service
public class AlertReportService {

    private final MockDataGateway gateway;
    private final FilterValidator filterValidator;

    public AlertReportService(MockDataGateway gateway, FilterValidator filterValidator) {
        this.gateway = gateway;
        this.filterValidator = filterValidator;
    }

    public AlertReportResponse getAlerts(ReportFilter filter) {
        filterValidator.validate(filter);

        List<MockAlert> matched = gateway.findAlerts(filter);

        List<AlertItemResponse> items = matched.stream()
                .map(a -> new AlertItemResponse(
                        a.getId(),
                        a.getSectionId(),
                        a.getCourseCode(),
                        a.getType(),
                        a.getRiskLevel(),
                        a.getStatus(),
                        a.getTitle(),
                        a.getGeneratedAt()))
                .collect(Collectors.toList());

        return new AlertReportResponse(items.size(), items);
    }
}
