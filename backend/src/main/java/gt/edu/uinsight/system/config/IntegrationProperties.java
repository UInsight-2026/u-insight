package gt.edu.uinsight.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuracion de los modulos que C7 consulta en {@code GET /api/v1/system/integration-status}.
 *
 * <p>Los valores por defecto viven en esta clase y no en {@code application.properties},
 * que es un archivo compartido entre las 21 celulas. Cualquiera se sobreescribe por
 * variable de entorno sin tocar el archivo, por ejemplo
 * {@code UINSIGHT_INTEGRATION_ENDPOINTS_A1} o
 * {@code UINSIGHT_INTEGRATION_DEGRADEDTHRESHOLDMS}.
 */
@Component
@ConfigurationProperties(prefix = "uinsight.integration")
public class IntegrationProperties {

    /**
     * U-Insight es un monolito: las 21 celulas se despliegan en la misma aplicacion y
     * escuchan en el unico puerto de {@code server.port}. No hay un puerto por celula.
     */
    private static final String BASE = "http://localhost:8080";

    /**
     * Umbral en milisegundos para considerar que un modulo esta DEGRADED.
     */
    private long degradedThresholdMs = 1000;

    /**
     * Tiempo de espera maximo en milisegundos antes de considerar un modulo DOWN.
     */
    private long timeoutMs = 3000;

    /**
     * Modulos a consultar, con la ruta de cada uno.
     *
     * <p>Las rutas estan elegidas a proposito entre las que responden 200 con la base de
     * datos vacia: son listados sin parametros obligatorios. Una ruta que necesite un id
     * existente devolveria 404 en un entorno limpio y el modulo se reportaria DOWN
     * estando sano.
     *
     * <p>Quedan fuera dos celulas, y es deliberado: <strong>B-analitica</strong>
     * ({@code /api/v1/analytics/...}) y <strong>C4</strong>
     * ({@code /api/v1/alerts/[alertId]/interventions}) solo exponen endpoints que exigen
     * un identificador existente, asi que no admiten un chequeo independiente de datos.
     * B queda representada por B7, cuyo endpoint existe justamente para integracion.
     */
    private Map<String, String> endpoints = new LinkedHashMap<>();

    public IntegrationProperties() {
        endpoints.put("A1", BASE + "/api/v1/academic-periods");
        endpoints.put("A2", BASE + "/api/v1/teachers");
        endpoints.put("A4", BASE + "/api/v1/sections");
        endpoints.put("A5", BASE + "/api/v1/evaluations");
        endpoints.put("A6", BASE + "/api/v1/grades/evaluation-refs");
        endpoints.put("B7", BASE + "/api/v1/integration/b7/active-rules");
        endpoints.put("C1", BASE + "/api/v1/indicator-configurations");
        endpoints.put("C3", BASE + "/api/v1/alert-rules");
        endpoints.put("C5", BASE + "/api/v1/reports/overview");
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
