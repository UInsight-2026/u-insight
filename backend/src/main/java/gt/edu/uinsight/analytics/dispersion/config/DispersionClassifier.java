
package gt.edu.uinsight.analytics.dispersion.config;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;

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
                || standardDeviation.compareTo(BigDecimal.ZERO) < 0) {
            throw new DispersionDatosInvalidosException(
                    "La desviación estándar no puede ser nula ni negativa.");
        }

        double lowValue = thresholds.getLow();
        double highValue = thresholds.getHigh();

        if (!Double.isFinite(lowValue)
                || !Double.isFinite(highValue)
                || lowValue < 0
                || highValue <= lowValue) {
            throw new IllegalStateException(
                    "Los umbrales de dispersión no están configurados correctamente.");
        }

        BigDecimal low = BigDecimal.valueOf(lowValue);
        BigDecimal high = BigDecimal.valueOf(highValue);

        if (standardDeviation.compareTo(low) < 0) {
            return DispersionClassification.LOW_DISPERSION;
        }

        if (standardDeviation.compareTo(high) < 0) {
            return DispersionClassification.MODERATE_DISPERSION;
        }

        return DispersionClassification.HIGH_DISPERSION;
    }
}
