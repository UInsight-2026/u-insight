package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.common.ReportLogger;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import gt.edu.uinsight.report.dto.response.SectionReportResponse;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import gt.edu.uinsight.report.exception.ResourceNotFoundException;
import gt.edu.uinsight.report.gateway.AnalyticsGateway;
import gt.edu.uinsight.report.gateway.ReportDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockSection;
import org.springframework.stereotype.Service;

/**
 * Reporte consolidado por sección (GET /api/v1/reports/sections/{id}).
 *
 * Semana 3: agrega estudiantes en riesgo, integración con
 * los datos analíticos de B6 y registro de la operación.
 */
@Service
public class SectionReportService {

    private static final String OPERATION = "GET_REPORTS_SECTION";
    private static final String PATH = "/api/v1/reports/sections/{id}";

    private final ReportDataGateway gateway;
    private final AnalyticsGateway analyticsGateway;
    private final FilterValidator filterValidator;
    private final ReportLogger reportLogger;

    public SectionReportService(
            ReportDataGateway gateway,
            AnalyticsGateway analyticsGateway,
            FilterValidator filterValidator,
            ReportLogger reportLogger) {
        this.gateway = gateway;
        this.analyticsGateway = analyticsGateway;
        this.filterValidator = filterValidator;
        this.reportLogger = reportLogger;
    }

    public SectionReportResponse getSectionReport(Long id, ReportFilter filter) {

        long startedAt = System.nanoTime();
        String traceId = reportLogger.start(OPERATION, PATH);

        try {
            filterValidator.validate(filter);
        } catch (InvalidFilterException ex) {
            reportLogger.rejected(traceId, OPERATION, ex.getMessage());
            throw ex;
        }

        MockSection section = gateway.findSectionById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro la seccion con id " + id));

        int activeAlerts = (int) gateway.findAlertsBySectionId(id).stream()
                .filter(MockAlert::isActive)
                .count();

        AnalyticsSnapshot analytics = analyticsGateway.getSectionAnalytics(id);

        reportLogger.success(
                traceId,
                OPERATION,
                startedAt,
                "sectionId=" + id
                        + " riskLevel=" + section.getRiskLevel()
                        + " activeAlerts=" + activeAlerts
                        + " analyticsAvailable=" + analytics.isAvailable());

        return new SectionReportResponse(
                section.getId(),
                section.getName(),
                section.getRiskLevel(),
                section.getStudentsAtRisk(),
                activeAlerts,
                analytics);
    }
}