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

import java.util.Set;

/**
 * Integra C5 con el motor de riesgo de la Celula B7, alimentado con la analitica
 * que ya entrega B6. Ningun fallo de B7 se propaga a los reportes.
 *
 * Se depende de RiskEngine y no de RiskEvaluationService a proposito: el servicio
 * persiste una fila de alerta en cada evaluacion, y un reporte es de solo lectura.
 * Consultar el listado de secciones no puede crear alertas como efecto colateral.
 *
 * B7 evalua cinco reglas y una de ellas es "percentil90 < 20". Ese dato no llega
 * en el AnalyticsSnapshot de B6, y el motor lo lee como double primitivo: dejarlo
 * en cero dispara la regla SIEMPRE y ninguna seccion podria salir en riesgo bajo.
 * Por eso se envia un valor neutro, y el resultado se marca B7_PARTIAL para que el
 * consumidor sepa que el nivel es valido pero se calculo con cuatro reglas de cinco.
 */
@Component
public class B7RiskGateway implements RiskGateway {

    private static final Logger log = LoggerFactory.getLogger(B7RiskGateway.class);

    /** Neutro frente a la regla "percentil90 < 20" de B7. Ver el javadoc de la clase. */
    private static final double PERCENTIL_90_NO_DISPONIBLE = 100.0;

    /** La escala que publica C5. Un valor fuera de ella no se traduce: se descarta. */
    private static final Set<String> NIVELES_DE_C5 = Set.of("LOW", "MEDIUM", "HIGH");

    private final AnalyticsGateway analyticsGateway;
    private final RiskEngine riskEngine;

    public B7RiskGateway(AnalyticsGateway analyticsGateway, RiskEngine riskEngine) {
        this.analyticsGateway = analyticsGateway;
        this.riskEngine = riskEngine;
    }

    @Override
    public RiskSnapshot getSectionRisk(Long sectionId) {
        AnalyticsSnapshot analitica = analyticsGateway.getSectionAnalytics(sectionId);

        // Las reglas de B7 son numericas: sin media no hay nada que evaluar.
        if (!analitica.isAvailable() || analitica.getMean() == null) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=sin analitica de B6", sectionId);
            return RiskSnapshot.unavailable();
        }

        try {
            RiskResult salida = riskEngine.evaluate(indicadoresPara(analitica));

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

            log.info("INTEGRATION_PARTIAL source=B7 sectionId={} riskLevel={} reglasActivadas={} "
                            + "percentil90Disponible=false",
                    sectionId, nivel, salida.getActivatedRules());
            return new RiskSnapshot(nivel, "B7_PARTIAL", true);

        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} message={}", sectionId, ex.getMessage());
            return RiskSnapshot.unavailable();
        }
    }

    /**
     * Los cuatro bloques se envian siempre completos. El motor los lee como double
     * primitivo sin comprobar nulos, de modo que un bloque ausente o un campo nulo
     * lo haria fallar con NullPointerException en lugar de devolver un nivel.
     *
     * El relleno de un indicador ausente no es el mismo para todos, porque cada
     * regla compara en un sentido distinto y un cero no siempre es inofensivo:
     *
     *   desviacion  la regla es "> 5", y cero no la dispara
     *   tendencia   la regla es "< 0", y cero no la dispara
     *   mediana     la regla es "< 60", y cero SI la dispara: un cero por ausencia
     *               sumaria 15 puntos de riesgo que nadie midio. Se usa la media,
     *               que es el otro indicador de tendencia central y si esta
     *               disponible, en vez de un umbral copiado de las reglas de B7
     */
    private SectionIndicatorsDto indicadoresPara(AnalyticsSnapshot analitica) {
        double media = analitica.getMean();
        double mediana = analitica.getMedian() != null ? analitica.getMedian() : media;

        return new SectionIndicatorsDto(
                new CentralTendencyDto(media, mediana),
                new PositionDto(PERCENTIL_90_NO_DISPONIBLE),
                new DispersionDto(neutro(analitica.getStandardDeviation())),
                new TrendDto(neutro(analitica.getAverageChange())),
                null);
    }

    /** Para las reglas "> 5" y "< 0", cero no dispara: es el relleno que no inclina nada. */
    private double neutro(Double valor) {
        return valor != null ? valor : 0.0;
    }
}
