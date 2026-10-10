package gt.edu.uinsight.alert.engine;

import java.util.ArrayList;
import java.util.List;
import gt.edu.uinsight.alert.dto.section.SectionIndicatorsDto;

public class RiskEngine {
        public RiskResult evaluate(SectionIndicatorsDto dto) {

        List<String> activatedRules = new ArrayList<>();
        double score = 0;
        int rulesEvaluated = 5;
        int rulesTriggered = 0;

        double media = dto.getCentralTendency().getMean();
        double mediana = dto.getCentralTendency().getMedian();
        double desviacion = dto.getDispersion().getStdDev();
        double tendencia = dto.getTrend().getValue();
        double percentil90 = dto.getPosition().getPercentile90();

        // REGLA #1: MEDIA
        if (media < 60) {
            activatedRules.add("MEDIA_BAJA");
            score += 20;
            rulesTriggered++;
        }

        // REGLA #2: MEDIANA
        if (mediana < 60) {
            activatedRules.add("MEDIANA_BAJA");
            score += 15;
            rulesTriggered++;
        }

        // REGLA #3: DESVIACION
        if (desviacion > 5) {
            activatedRules.add("DESVIACION_ALTA");
            score += 10;
            rulesTriggered++;
        }
        // REGLA #4: TENDENCIA
        if (tendencia < 0) {
            activatedRules.add("TENDENCIA_NEGATIVA");
            score += 25;
            rulesTriggered++;
        }

        // REGLA #5: PERCENTIL 90
        if (percentil90 < 20) {
            activatedRules.add("PERCENTIL90_BAJO");
            score += 30;
            rulesTriggered++;
        }

        // NIVEL DE RIESGO
        String riskLevel;

        if (score >= 60) {
            riskLevel = "HIGH";
        } else if (score >= 30) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }

        return new RiskResult(
                riskLevel,
                score,
                rulesEvaluated,
                rulesTriggered,
                rulesTriggered,
                activatedRules
        );
    }
}
