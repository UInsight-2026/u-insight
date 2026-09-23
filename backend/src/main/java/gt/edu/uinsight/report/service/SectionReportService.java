package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.SectionReportResponse;
import gt.edu.uinsight.report.exception.ResourceNotFoundException;
import gt.edu.uinsight.report.mock.MockDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockSection;
import org.springframework.stereotype.Service;

/**
 * Reporte consolidado por seccion (GET /api/v1/reports/sections/{id}).
 */
@Service
public class SectionReportService {

    private final MockDataGateway gateway;
    private final FilterValidator filterValidator;

    public SectionReportService(MockDataGateway gateway, FilterValidator filterValidator) {
        this.gateway = gateway;
        this.filterValidator = filterValidator;
    }

    public SectionReportResponse getSectionReport(Long id, ReportFilter filter) {
        filterValidator.validate(filter);

        MockSection section = gateway.findSectionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la seccion con id " + id));

        int activeAlerts = (int) gateway.findAlertsBySectionId(id).stream()
                .filter(MockAlert::isActive)
                .count();

        return new SectionReportResponse(
                section.getId(),
                section.getName(),
                section.getRiskLevel(),
                activeAlerts);
    }
}
