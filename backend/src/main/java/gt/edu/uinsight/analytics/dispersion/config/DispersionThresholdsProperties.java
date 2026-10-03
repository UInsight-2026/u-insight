package gt.edu.uinsight.analytics.dispersion.config;
 
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
 
@Component
@ConfigurationProperties(prefix = "dispersion.thresholds")
public class DispersionThresholdsProperties {
 
    private double low = 10.0;
    private double high = 30.0;
 
    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }
    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }
}
