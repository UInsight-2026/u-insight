package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.alert.dto.section.CentralTendencyDto;
import gt.edu.uinsight.alert.dto.section.DispersionDto;
import gt.edu.uinsight.alert.dto.section.PositionDto;
import gt.edu.uinsight.alert.dto.section.SectionIndicatorsDto;
import gt.edu.uinsight.alert.dto.section.TrendDto;
import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import gt.edu.uinsight.report.dto.response.RiskSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Integra C5 con el motor de riesgo (paquete {@code alert}, antes B7), alimentado
 * con la analitica que ya entrega B6. Ningun fallo del motor se propaga a los
 * reportes.
 *
 * El motor evalua cinco reglas y una de ellas es "percentil90 &lt; 20". Ese dato
 * no llega en el AnalyticsSnapshot de B6, y como PositionDto.percentile90 se
 * desempaqueta a double primitivo dentro de RiskEngine, dejarlo en null causaria
 * NullPointerException (y en 0.0 la regla se dispararia SIEMPRE: con eso ninguna
 * seccion podia salir en riesgo bajo). Por eso se envia un valor neutro que no
 * dispara la regla, y el resultado se marca B7_PARTIAL para que el consumidor
 * sepa que el nivel es valido pero se calculo con cuatro reglas de cinco.
 */
@Component
public class B7RiskGateway implements RiskGateway {

    private static final Logger log = LoggerFactory.getLogger(B7RiskGateway.class);

    /** Neutro frente a la regla "percentil90 < 20" del motor. Ver el javadoc de la clase. */
    private static final double PERCENTIL_90_NO_DISPONIBLE = 100.0;

    private final AnalyticsGateway analyticsGateway;
    private final RiskEngine riskEngine;

    public B7RiskGateway(AnalyticsGateway analyticsGateway, RiskEngine riskEngine) {
        this.analyticsGateway = analyticsGateway;
        this.riskEngine = riskEngine;
    }

    @Override
    public RiskSnapshot getSectionRisk(Long sectionId) {
        AnalyticsSnapshot analitica = analyticsGateway.getSectionAnalytics(sectionId);

        // Las reglas numericas del motor necesitan al menos la media: sin ella no hay nada que evaluar.
        if (!analitica.isAvailable() || analitica.getMean() == null) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=sin analitica de B6", sectionId);
            return RiskSnapshot.unavailable();
        }

        try {
            RiskResult salida = riskEngine.evaluate(entradaPara(analitica));

            if (salida == null || salida.getRiskLevel() == null) {
                log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=respuesta sin nivel", sectionId);
                return RiskSnapshot.unavailable();
            }

            // RiskEngine ya devuelve el nivel en la escala de C5 (LOW/MEDIUM/HIGH),
            // no hace falta traducir desde espanol como en la version anterior del motor.
            log.info("INTEGRATION_PARTIAL source=B7 sectionId={} riskLevel={} reglasActivadas={} "
                            + "percentil90Disponible=false",
                    sectionId, salida.getRiskLevel(), salida.getRulesTriggered());
            return new RiskSnapshot(salida.getRiskLevel(), "B7_PARTIAL", true);

        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} message={}", sectionId, ex.getMessage());
            return RiskSnapshot.unavailable();
        }
    }

    private SectionIndicatorsDto entradaPara(AnalyticsSnapshot analitica) {
        SectionIndicatorsDto entrada = new SectionIndicatorsDto();
        entrada.setCentralTendency(new CentralTendencyDto(analitica.getMean(), valorO(analitica.getMedian())));
        entrada.setDispersion(new DispersionDto(valorO(analitica.getStandardDeviation())));
        entrada.setTrend(new TrendDto(valorO(analitica.getAverageChange())));
        entrada.setPosition(new PositionDto(PERCENTIL_90_NO_DISPONIBLE));
        return entrada;
    }

    /** Solo media, desviacion, tendencia y percentil90 alimentan reglas; 0.0 es neutro. */
    private double valorO(Double valor) {
        return valor != null ? valor : 0.0;
    }
}
