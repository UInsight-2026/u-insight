package gt.edu.uinsight.analytics.dispersion.config;
 
import 
gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import org.springframework.stereotype.Component;
 
import java.math.BigDecimal;
 
@Component
public class DispersionClassifier {
 
    private final DispersionThresholdsProperties thresholds;
 
    public DispersionClassifier(DispersionThresholdsProperties thresholds) {
        this.thresholds = thresholds;
    }
 
    public DispersionClassification clasificar(BigDecimal standardDeviation) {
        if (standardDeviation == null) {
            throw new IllegalArgumentException("La desviación estándar no puede 
ser nula.");
        }
        double valor = standardDeviation.doubleValue();
        if (valor < thresholds.getLow()) {
            return DispersionClassification.LOW_DISPERSION;
        }
        if (valor <= thresholds.getHigh()) {
            return DispersionClassification.MODERATE_DISPERSION;
        }
        return DispersionClassification.HIGH_DISPERSION;
    }
}
