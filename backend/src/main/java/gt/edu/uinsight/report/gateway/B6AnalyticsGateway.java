package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.analytics.summary.service.SummaryService;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Integra C5 con B6 sin propagar fallos de la dependencia a los reportes. */
@Component
public class B6AnalyticsGateway implements AnalyticsGateway {
    private static final Logger log = LoggerFactory.getLogger(B6AnalyticsGateway.class);

    /**
     * La implementacion actual del modulo de dispersion devuelve valores constantes
     * en lugar de calcularlos sobre las notas de la seccion. Mientras siga asi, C5
     * publica el dato pero lo marca: no se omite, porque el consumidor puede
     * necesitarlo, y no se presenta como calculado, porque no lo es.
     * Cuando el origen calcule de verdad, esta constante pasa a "B3".
     */
    private static final String ORIGEN_DISPERSION = "B3_FIXED";

    private final SummaryService summaryService;

    public B6AnalyticsGateway(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @Override
    public AnalyticsSnapshot getSectionAnalytics(Long sectionId) {
        try {
            var summary = summaryService.getSummary(sectionId);
            if (summary == null) {
                log.warn("INTEGRATION_ERROR source=B6 sectionId={} reason=respuesta nula", sectionId);
                return AnalyticsSnapshot.unavailable(sectionId);
            }
            var central = summary.getCentralTendencyData();
            var dispersion = summary.getDispersionData();
            var trend = summary.getTrendData();
            // B6 captura errores por componente. Una respuesta no nula puede
            // carecer de todos los componentes que consume C5.
            if (central == null && dispersion == null && trend == null) {
                log.warn("INTEGRATION_ERROR source=B6 sectionId={} reason=sin analitica disponible", sectionId);
                return AnalyticsSnapshot.unavailable(sectionId);
            }
            var snapshot = new AnalyticsSnapshot(
                    sectionId,
                    central != null ? central.mean() : null,
                    central != null ? central.median() : null,
                    central != null ? central.sampleSize() : null,
                    dispersion != null ? dispersion.standardDeviation() : null,
                    trend != null && trend.classification() != null ? trend.classification().name() : null,
                    trend != null && trend.averageChange() != null ? trend.averageChange().doubleValue() : null,
                    dispersion != null ? ORIGEN_DISPERSION : AnalyticsSnapshot.DISPERSION_NO_DISPONIBLE,
                    true);
            // available indica que hay al menos un componente; no garantiza
            // que la tendencia u otro indicador concreto este disponible.
            if (central == null || dispersion == null || trend == null) {
                log.warn("INTEGRATION_PARTIAL source=B6 sectionId={} unavailableComponents={}",
                        sectionId, summary.getUnavailableComponents());
            } else {
                log.info("INTEGRATION_OK source=B6 sectionId={} trend={}",
                        sectionId, snapshot.getTrendClassification());
            }
            return snapshot;
        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source=B6 sectionId={} message={}", sectionId, ex.getMessage());
            return AnalyticsSnapshot.unavailable(sectionId);
        }
    }
}
