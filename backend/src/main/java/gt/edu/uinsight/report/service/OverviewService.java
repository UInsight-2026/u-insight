package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.OverviewResponse;
import gt.edu.uinsight.report.mock.MockDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockSection;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reporte general del sistema (GET /reports/overview).
 * Integrante 1 - Angel (Coordinador + Overview).
 */
@Service
public class OverviewService {

    private final MockDataGateway gateway;
    private final FilterValidator filterValidator;

    public OverviewService(MockDataGateway gateway, FilterValidator filterValidator) {
        this.gateway = gateway;
        this.filterValidator = filterValidator;
    }

    public OverviewResponse getOverview(ReportFilter filter) {
        filterValidator.validate(filter);

        List<MockSection> sections = gateway.findSections(filter);
        List<MockAlert> alerts = gateway.findAlerts(filter);

        int highRiskSections = (int) sections.stream()
                .filter(s -> "HIGH".equalsIgnoreCase(s.getRiskLevel()))
                .count();
        int lowRiskSections = (int) sections.stream()
                .filter(s -> "LOW".equalsIgnoreCase(s.getRiskLevel()))
                .count();
        int studentsAtRisk = sections.stream()
                .mapToInt(MockSection::getStudentsAtRisk)
                .sum();
        int activeAlerts = (int) alerts.stream()
                .filter(MockAlert::isActive)
                .count();

        String overallTrend = calculateTrend(highRiskSections, lowRiskSections);

        return new OverviewResponse(activeAlerts, highRiskSections, studentsAtRisk, overallTrend);
    }

    /**
     * Regla TEMPORAL y simplificada solo para el mock: compara secciones en
     * alto riesgo contra secciones en bajo riesgo. En el sistema real,
     * overallTrend debe venir del servicio de tendencia de la Seccion B
     * (B4 - Evolucion y tendencia / B6 - Consolidacion analitica), no de
     * este calculo.
     */
    private String calculateTrend(int highRiskSections, int lowRiskSections) {
        if (highRiskSections > lowRiskSections) {
            return "NEGATIVE";
        } else if (highRiskSections < lowRiskSections) {
            return "POSITIVE";
        }
        return "STABLE";
    }
}
