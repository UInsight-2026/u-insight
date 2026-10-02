package gt.edu.uinsight.system.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "uinsight.integration")
public class IntegrationProperties {

    /**
     * Umbral en milisegundos para considerar que un servicio está DEGRADED.
     */
    private long degradedThresholdMs = 1000;

    /**
     * Tiempo de espera máximo (timeout) en milisegundos antes de considerar un servicio DOWN.
     */
    private long timeoutMs = 3000;

    /**
     * Mapa de módulos reales a consultar con sus endpoints por defecto.
     */
    private Map<String, String> endpoints = new LinkedHashMap<>();

    public IntegrationProperties() {
        // Rutas reales requeridas para el entregable
        endpoints.put("A1", "http://localhost:8081/api/v1/academic-periods");
        endpoints.put("A2", "http://localhost:8082/api/v1/teachers");
        endpoints.put("A5", "http://localhost:8085/api/v1/evaluations");
        endpoints.put("A6", "http://localhost:8086/api/v1/grades");
        endpoints.put("B-ANALYTICS", "http://localhost:8087/api/v1/analytics/status");
        endpoints.put("C1", "http://localhost:8088/api/v1/indicators/config");
        endpoints.put("C4", "http://localhost:8089/api/v1/interventions");
        endpoints.put("C5", "http://localhost:8090/api/v1/reports");
    }

    public long getDegradedThresholdMs() {
        return degradedThresholdMs;
    }

    public void setDegradedThresholdMs(long degradedThresholdMs) {
        this.degradedThresholdMs = degradedThresholdMs;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Map<String, String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, String> endpoints) {
        this.endpoints = endpoints;
    }
}