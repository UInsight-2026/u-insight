package gt.edu.uinsight.analytics.dispersion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Umbrales configurables para clasificar el nivel de dispersión,
 * en lugar de valores codificados de forma rígida (hardcoded).
 *
 * Se pueden sobreescribir en application.yml / application.properties con:
 *
 *   dispersion.thresholds.low: 10.0
 *   dispersion.thresholds.high: 30.0
 *
 * Si no se definen, se usan los valores por defecto de abajo.
 *
 * Responsable: Zarbya Yanina Hernandez Hernandez
 */
@Component
@ConfigurationProperties(prefix = "dispersion.thresholds")
public class DispersionThresholdsProperties {

    /** Desviación estándar por debajo de este valor => LOW_DISPERSION. */
    private double low = 10.0;

    /** Desviación estándar por encima de este valor => HIGH_DISPERSION. */
    private double high = 30.0;

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }
}
