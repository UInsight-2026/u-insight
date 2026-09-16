package gt.edu.uinsight.imports.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "app.imports")
public class CsvImportProperties {

    /** Tamaño máximo permitido para el archivo CSV, en megabytes. */
    private long maxFileSizeMb = 5;

    /** Extensión de archivo permitida para la carga. */
    private String allowedExtension = ".csv";

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
}