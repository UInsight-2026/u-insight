package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.alert.b7.model.RiskInput;
import gt.edu.uinsight.alert.b7.model.RiskOutput;
import gt.edu.uinsight.alert.b7.Service.RiskEngineService;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import gt.edu.uinsight.report.dto.response.RiskSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Integra C5 con el motor de riesgo de la Celula B7, alimentado con la analitica
 * que ya entrega B6. Ningun fallo de B7 se propaga a los reportes.
 *
 * B7 evalua tres reglas y una de ellas es "percentil90 < 20". Ese dato no llega
 * en el AnalyticsSnapshot de B6, y como RiskInput.percentil90 es un double
 * primitivo, dejarlo sin asignar vale 0.0 y la regla se disparaba SIEMPRE: con
 * eso ninguna seccion podia salir en riesgo bajo. Por eso se envia un valor
 * neutro que no dispara la regla, y el resultado se marca B7_PARTIAL para que el
 * consumidor sepa que el nivel es valido pero se calculo con dos reglas de tres.
 */
@Component
public class B7RiskGateway implements RiskGateway {

    private static final Logger log = LoggerFactory.getLogger(B7RiskGateway.class);

    /** Neutro frente a la regla "percentil90 < 20" de B7. Ver el javadoc de la clase. */
    private static final double PERCENTIL_90_NO_DISPONIBLE = 100.0;

    private final AnalyticsGateway analyticsGateway;
    private final RiskEngineService riskEngineService;

    public B7RiskGateway(AnalyticsGateway analyticsGateway, RiskEngineService riskEngineService) {
        this.analyticsGateway = analyticsGateway;
        this.riskEngineService = riskEngineService;
    }

    @Override
    public RiskSnapshot getSectionRisk(Long sectionId) {
        AnalyticsSnapshot analitica = analyticsGateway.getSectionAnalytics(sectionId);

        // Las tres reglas de B7 son numericas: sin media no hay nada que evaluar.
        if (!analitica.isAvailable() || analitica.getMean() == null) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=sin analitica de B6", sectionId);
            return RiskSnapshot.unavailable();
        }

        try {
            RiskOutput salida = riskEngineService.evaluarRiesgo(entradaPara(analitica));

            if (salida == null || salida.getNivelRiesgo() == null) {
                log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=respuesta sin nivel", sectionId);
                return RiskSnapshot.unavailable();
            }

            String nivel = aEscalaDeC5(salida.getNivelRiesgo());
            if (nivel == null) {
                log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=nivel desconocido '{}'",
                        sectionId, salida.getNivelRiesgo());
                return RiskSnapshot.unavailable();
            }

            log.info("INTEGRATION_PARTIAL source=B7 sectionId={} riskLevel={} reglasActivadas={} "
                            + "percentil90Disponible=false",
                    sectionId, nivel, salida.getReglasActivadas());
            return new RiskSnapshot(nivel, "B7_PARTIAL", true);

        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} message={}", sectionId, ex.getMessage());
            return RiskSnapshot.unavailable();
        }
    }

    private RiskInput entradaPara(AnalyticsSnapshot analitica) {
        RiskInput entrada = new RiskInput();
        entrada.setMedia(analitica.getMean());
        entrada.setMediana(valorO(analitica.getMedian()));
        entrada.setDesviacion(valorO(analitica.getStandardDeviation()));
        entrada.setTendencia(valorO(analitica.getAverageChange()));
        entrada.setPercentil90(PERCENTIL_90_NO_DISPONIBLE);
        return entrada;
    }

    /**
     * B7 responde en espanol (BAJO, MODERADO, ALTO) y C5 publica LOW, MEDIUM y
     * HIGH, que son los valores que valida FilterValidator y los que espera el
     * frontend. La traduccion vive aqui para que el vocabulario de B7 no llegue a
     * los servicios de reportes.
     */
    private String aEscalaDeC5(String nivelDeB7) {
        return switch (nivelDeB7.trim().toUpperCase()) {
            case "BAJO" -> "LOW";
            case "MODERADO" -> "MEDIUM";
            case "ALTO" -> "HIGH";
            default -> null;
        };
    }

    /** Solo media, desviacion, tendencia y percentil90 alimentan reglas; 0.0 es neutro. */
    private double valorO(Double valor) {
        return valor != null ? valor : 0.0;
    }
}
