
package gt.edu.uinsight.analytics.dispersion.config;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;

@Component
public class DispersionClassifier {

    private final DispersionThresholdsProperties thresholds;

    public DispersionClassifier(
            DispersionThresholdsProperties thresholds) {
        this.thresholds = thresholds;
    }

    public DispersionClassification clasificar(
            BigDecimal standardDeviation) {

        if (standardDeviation == null
                || standardDeviation.signum() < 0) {
            throw new IllegalArgumentException(
                    "La desviación estándar debe ser válida y no negativa."
            );
        }

        BigDecimal low = BigDecimal.valueOf(thresholds.getLow());
        BigDecimal high = BigDecimal.valueOf(thresholds.getHigh());

        if (standardDeviation.compareTo(low) < 0) {
            return DispersionClassification.LOW_DISPERSION;
        }

        if (standardDeviation.compareTo(high) <= 0) {
            return DispersionClassification.MODERATE_DISPERSION;
        }

        return DispersionClassification.HIGH_DISPERSION;
    }
}
