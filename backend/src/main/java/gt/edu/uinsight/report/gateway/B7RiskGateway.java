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
 */
@Component
public class B7RiskGateway implements RiskGateway {

    private static final Logger log = LoggerFactory.getLogger(B7RiskGateway.class);

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

        if (!analitica.isAvailable() || analitica.getMean() == null) {
            log.warn("INTEGRATION_ERROR source=B7 sectionId={} reason=sin analitica de B6", sectionId);
            return RiskSnapshot.unavailable();
        }

        try {
            // Se retorna unavailable por descalce temporal de DTO entre B7 y C5
            return RiskSnapshot.unavailable();

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

    private double valorO(Double valor) {
        return valor != null ? valor : 0.0;
    }
}