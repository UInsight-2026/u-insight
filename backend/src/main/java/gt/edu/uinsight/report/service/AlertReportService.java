//semana 3
package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.common.ReportLogger;
import gt.edu.uinsight.report.dto.filter.PageFilter;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AlertItemResponse;
import gt.edu.uinsight.report.dto.response.AlertReportResponse;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import gt.edu.uinsight.report.gateway.ReportDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Service
public class AlertReportService {

    private static final String OPERATION = "GET_REPORTS_ALERTS";
    private static final String PATH = "/api/v1/reports/alerts";

    /** Peso de cada nivel para el orden por riesgo. */
    private static final Map<String, Integer> RISK_WEIGHT =
            Map.of("HIGH", 3, "MEDIUM", 2, "LOW", 1);

    private final ReportDataGateway gateway;
    private final FilterValidator filterValidator;
    private final ReportLogger reportLogger;

    public AlertReportService(ReportDataGateway gateway,
                              FilterValidator filterValidator,
                              ReportLogger reportLogger) {
        this.gateway = gateway;
        this.filterValidator = filterValidator;
        this.reportLogger = reportLogger;
    }

    public AlertReportResponse getAlerts(ReportFilter filter, PageFilter pageFilter) {
        long startedAt = System.nanoTime();
        String traceId = reportLogger.start(OPERATION, PATH);
        try {
            filterValidator.validate(filter);
            filterValidator.validatePage(pageFilter);
        } catch (InvalidFilterException ex) {
            reportLogger.rejected(traceId, OPERATION, ex.getMessage());
            throw ex;
        }

        List<MockAlert> encontradas = gateway.findAlerts(filter);
        List<AlertItemResponse> ordenadas = encontradas.stream()
                .sorted(comparadorDe(pageFilter.getSort()))
                .map(this::aItemDeRespuesta)
                .toList();

        int total = ordenadas.size();
        int size = pageFilter.getSize();
        int totalPages = (total == 0) ? 0 : (int) Math.ceil((double) total / size);

        int desde = Math.min(pageFilter.getPage() * size, total);
        int hasta = Math.min(desde + size, total);
        List<AlertItemResponse> pagina = ordenadas.subList(desde, hasta);

        reportLogger.success(traceId, OPERATION, startedAt,
                "total=" + total
                        + " page=" + pageFilter.getPage()
                        + " size=" + size
                        + " totalPages=" + totalPages
                        + " sort=" + pageFilter.getSort()
                        + " devueltas=" + pagina.size());

        return new AlertReportResponse(total, pageFilter.getPage(), size, totalPages, pagina);
    }

    /**
     * generatedAt viene en formato ISO (2026-09-11T10:05:00), así que el
     * orden alfabético coincide con el orden cronológico y no hace falta
     * convertir a fecha.
     */
    private Comparator<MockAlert> comparadorDe(String sort) {
        Comparator<MockAlert> porFecha = Comparator.comparing(MockAlert::getGeneratedAt);
        return switch (sort) {
            case "OLDEST" -> porFecha;
            case "RISK" -> Comparator
                    .comparingInt((MockAlert a) -> pesoDelRiesgo(a.getRiskLevel()))
                    .thenComparing(porFecha)
                    .reversed();
            default -> porFecha.reversed(); // NEWEST
        };
    }

    private int pesoDelRiesgo(String riskLevel) {
        if (riskLevel == null) {
            return 0;
        }
        return RISK_WEIGHT.getOrDefault(riskLevel.toUpperCase(), 0);
    }

    private AlertItemResponse aItemDeRespuesta(MockAlert alerta) {
        return new AlertItemResponse(
                alerta.getId(),
                alerta.getSectionId(),
                alerta.getCourseCode(),
                alerta.getType(),
                alerta.getRiskLevel(),
                alerta.getStatus(),
                alerta.getTitle(),
                alerta.getGeneratedAt());
    }
}