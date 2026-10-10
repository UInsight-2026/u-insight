package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.common.ReportLogger;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import gt.edu.uinsight.report.dto.response.OverviewResponse;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import gt.edu.uinsight.report.gateway.AnalyticsGateway;
import gt.edu.uinsight.report.gateway.ReportDataGateway;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockSection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Reporte general del sistema (GET /api/v1/reports/overview).
 *
 * Semana 3: overallTrend deja de calcularse localmente y se consulta a
 * la Celula B6. Si B6 no responde para ninguna seccion, se usa la regla
 * local anterior y se avisa en la respuesta.
 */
@Service
public class OverviewService {

    private static final String OPERATION = "GET_REPORTS_OVERVIEW";
    private static final String PATH = "/api/v1/reports/overview";

    private final ReportDataGateway gateway;
    private final AnalyticsGateway analyticsGateway;
    private final FilterValidator filterValidator;
    private final ReportLogger reportLogger;

    public OverviewService(ReportDataGateway gateway,
                           AnalyticsGateway analyticsGateway,
                           FilterValidator filterValidator,
                           ReportLogger reportLogger) {
        this.gateway = gateway;
        this.analyticsGateway = analyticsGateway;
        this.filterValidator = filterValidator;
        this.reportLogger = reportLogger;
    }

    public OverviewResponse getOverview(ReportFilter filter) {
        long startedAt = System.nanoTime();
        String traceId = reportLogger.start(OPERATION, PATH);

        try {
            filterValidator.validate(filter);
        } catch (InvalidFilterException ex) {
            reportLogger.rejected(traceId, OPERATION, ex.getMessage());
            throw ex;
        }

        List<MockSection> sections = gateway.findSections(filter);
        List<MockAlert> alerts = gateway.findAlerts(filter);

        int highRiskSections = (int) sections.stream()
                .filter(s -> "HIGH".equalsIgnoreCase(s.getRiskLevel()))
                .count();
        int lowRiskSections = (int) sections.stream()
                .filter(s -> "LOW".equalsIgnoreCase(s.getRiskLevel()))
                .count();
        // Semana 4: con datos reales el nivel lo calcula B7 y puede faltar. Esas
        // secciones se cuentan aparte; no se dan por buenas.
        int unknownRiskSections = (int) sections.stream()
                .filter(s -> s.getRiskLevel() == null)
                .count();
        int studentsAtRisk = sections.stream()
                .mapToInt(MockSection::getStudentsAtRisk)
                .sum();
        int activeAlerts = (int) alerts.stream()
                .filter(MockAlert::isActive)
                .count();

        List<String> unavailableSources = declararOrigenesFaltantes(sections, studentsAtRisk);
        String overallTrend;
        String trendSource;

        if (sections.isEmpty()) {
            // Sin secciones en el alcance no hay tendencia que consultar.
            overallTrend = "INSUFFICIENT_DATA";
            trendSource = "NONE";
        } else {
            String trendDeB6 = resolveTrendFromB6(sections);
            if (trendDeB6 != null) {
                overallTrend = trendDeB6;
                trendSource = "B6";
            } else {
                overallTrend = calculateTrendLocally(highRiskSections, lowRiskSections);
                trendSource = "LOCAL_FALLBACK";
                unavailableSources.add("B6");
            }
        }

        reportLogger.success(traceId, OPERATION, startedAt,
                "activeAlerts=" + activeAlerts
                        + " highRiskSections=" + highRiskSections
                        + " unknownRiskSections=" + unknownRiskSections
                        + " studentsAtRisk=" + studentsAtRisk
                        + " overallTrend=" + overallTrend
                        + " trendSource=" + trendSource
                        + " unavailableSources=" + unavailableSources);

        return new OverviewResponse(activeAlerts, highRiskSections, unknownRiskSections,
                studentsAtRisk, overallTrend, trendSource, unavailableSources);
    }

    /**
     * Declara que origenes de datos no respondieron, deduciendolo de lo que el
     * gateway alcanzo a resolver: deja en null lo que no pudo traer.
     *
     * Se deduce en lugar de preguntarlo porque la interfaz ReportDataGateway no
     * reporta el estado de cada fuente. Mientras no lo haga, esta es la lectura
     * mas fiel que se puede dar al consumidor.
     */
    private List<String> declararOrigenesFaltantes(List<MockSection> sections, int studentsAtRisk) {
        List<String> faltantes = new ArrayList<>();
        if (sections.isEmpty()) {
            return faltantes;
        }
        if (sections.stream().anyMatch(s -> s.getPeriod() == null)) {
            faltantes.add("A1");
        }
        if (sections.stream().anyMatch(s -> s.getTeacherCode() == null)) {
            faltantes.add("A2");
        }
        if (sections.stream().anyMatch(s -> s.getRiskLevel() == null)) {
            faltantes.add("B7");
        }
        // studentsAtRisk necesita los estudiantes de A3 y sus notas; sin eso no
        // se puede calcular y queda en cero.
        if (studentsAtRisk == 0) {
            faltantes.add("A3");
        }
        return faltantes;
    }

    /**
     * Pregunta a B6 la tendencia de cada seccion del alcance y resuelve la
     * tendencia global por mayoria simple.
     *
     * Devuelve null si ninguna seccion trajo tendencia, para que el
     * llamador sepa que debe recurrir a la regla local.
     */
    private String resolveTrendFromB6(List<MockSection> sections) {
        int negativas = 0;
        int positivas = 0;
        int respondidas = 0;

        for (MockSection section : sections) {
            AnalyticsSnapshot snapshot = analyticsGateway.getSectionAnalytics(section.getId());

            if (!snapshot.isAvailable() || snapshot.getTrendClassification() == null) {
                continue;
            }
            respondidas++;

            if ("NEGATIVE".equalsIgnoreCase(snapshot.getTrendClassification())) {
                negativas++;
            } else if ("POSITIVE".equalsIgnoreCase(snapshot.getTrendClassification())) {
                positivas++;
            }
        }

        if (respondidas == 0) {
            return null;
        }
        if (negativas > positivas) {
            return "NEGATIVE";
        }
        if (positivas > negativas) {
            return "POSITIVE";
        }
        return "STABLE";
    }

    /**
     * Regla local de respaldo, la misma de la Semana 2: compara secciones
     * en alto riesgo contra secciones en bajo riesgo. Solo se usa cuando
     * B6 no esta disponible.
     */
    private String calculateTrendLocally(int highRiskSections, int lowRiskSections) {
        if (highRiskSections > lowRiskSections) {
            return "NEGATIVE";
        }
        if (highRiskSections < lowRiskSections) {
            return "POSITIVE";
        }
        return "STABLE";
    }
}
