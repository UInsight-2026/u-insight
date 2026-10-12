package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import gt.edu.uinsight.report.dto.response.RiskSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Integra C5 con el motor de riesgo de la Celula B7. Ningun fallo de B7 se
 * propaga a los reportes.
 *
 * El motor pasa a recibir el SectionSummary de B6 en lugar de un DTO propio, de
 * modo que C5 ya no traduce indicadores: le pide el resumen a B6 y se lo entrega
 * tal cual. Eso elimina la conversion intermedia y, con ella, la posibilidad de
 * que C5 altere sin querer los valores que B7 evalua.
 *
 * Se depende de RiskEngine y no de RiskEvaluationService a proposito: el
 * servicio persiste una fila de alerta en cada evaluacion, y un reporte es de
 * solo lectura. Consultar el listado de secciones no puede crear alertas.
 */
@Component
public class B7RiskGateway implements RiskGateway {

    private static final Logger log = LoggerFactory.getLogger(B7RiskGateway.class);

    /** La escala que publica C5. Un valor fuera de ella no se traduce: se descarta. */
    private static final Set<String> NIVELES_DE_C5 = Set.of("LOW", "MEDIUM", "HIGH");

    private final SummaryService summaryService;
    private final RiskEngine riskEngine;

    public B7RiskGateway(SummaryService summaryService, RiskEngine riskEngine) {
        this.summaryService = summaryService;
        this.riskEngine = riskEngine;
    }

    @Override
    public RiskSnapshot getSectionRisk(Long sectionId) {
        SectionSummary resumen;
        try {
            resumen = summaryService.getSummary(sectionId);
        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source=B6 sectionId={} message={}", sectionId, ex.getMessage());
            return RiskSnapshot.unavailable();
        }

        // Sin resumen no hay nada que evaluar, y un riesgo inventado es peor que
        // un riesgo ausente: se declara no disponible y el reporte sigue.
        if (resumen == null || resumen.getCentralTendencyData() == null) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=sin analitica de B6", sectionId);
            return RiskSnapshot.unavailable();
        }

        try {
            RiskResult salida = riskEngine.evaluate(resumen);

            if (salida == null || salida.getRiskLevel() == null) {
                log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=respuesta sin nivel", sectionId);
                return RiskSnapshot.unavailable();
            }

            String nivel = salida.getRiskLevel().trim().toUpperCase();
            if (!NIVELES_DE_C5.contains(nivel)) {
                log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=nivel desconocido '{}'",
                        sectionId, salida.getRiskLevel());
                return RiskSnapshot.unavailable();
            }

            log.info("INTEGRATION_PARTIAL source=B7 sectionId={} riskLevel={} reglasEvaluadas={} "
                            + "reglasActivadas={}",
                    sectionId, nivel, salida.getRulesEvaluated(), salida.getActivatedRules());
            return new RiskSnapshot(nivel, "B7_PARTIAL", true);

        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} message={}", sectionId, ex.getMessage());
            return RiskSnapshot.unavailable();
        }
    }
}
