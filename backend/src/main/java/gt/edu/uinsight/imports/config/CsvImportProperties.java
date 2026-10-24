package gt.edu.uinsight.imports.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "app.imports")
public class CsvImportProperties {

    private long maxFileSizeMb = 5;

    private String allowedExtension = ".csv";

    private BigDecimal minScore = BigDecimal.ZERO;

    private BigDecimal maxScore = BigDecimal.valueOf(100);

    public long getMaxFileSizeMb() {
        return maxFileSizeMb;
    }

    public void setMaxFileSizeMb(long maxFileSizeMb) {
        this.maxFileSizeMb = maxFileSizeMb;
    }

    public String getAllowedExtension() {
        return allowedExtension;
    }

    public void setAllowedExtension(String allowedExtension) {
        this.allowedExtension = allowedExtension;
    }

    public BigDecimal getMinScore() {
        return minScore;
    }

    public void setMinScore(BigDecimal minScore) {
        this.minScore = minScore;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }
}
