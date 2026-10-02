package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.config.IntegrationProperties;
import gt.edu.uinsight.system.dto.request.CreateCheckRequest;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.logging.SystemEventLogger;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IntegrationStatusService {

    private final IntegrationProperties properties;
    private final SystemCheckService systemCheckService;
    private final SystemEventLogger systemEventLogger;
    private final RestClient restClient;

 public IntegrationStatusService(
            IntegrationProperties properties,
            SystemCheckService systemCheckService,
            SystemEventLogger systemEventLogger,
            RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.systemCheckService = systemCheckService;
        this.systemEventLogger = systemEventLogger;

        this.restClient = restClientBuilder.build();
    }

    public List<ModuleStatus> getIntegrationStatus() {
        List<ModuleStatus> statusList = new ArrayList<>();

        for (Map.Entry<String, String> entry : properties.getEndpoints().entrySet()) {
            String moduleName = entry.getKey();
            String url = entry.getValue();

            long startTime = System.currentTimeMillis();
            CheckStatus status;
            String message;

            try {
                var response = restClient.get()
                        .uri(url)
                        .retrieve()
                        .toBodilessEntity();

                long duration = System.currentTimeMillis() - startTime;

                if (response.getStatusCode().is2xxSuccessful()) {
                    status = (duration > properties.getDegradedThresholdMs()) ? CheckStatus.DEGRADED : CheckStatus.UP;
                    message = "Respondió correctamente (" + response.getStatusCode().value() + ")";
                } else {
                    status = CheckStatus.DOWN;
                    message = "Respuesta no exitosa (" + response.getStatusCode().value() + ")";
                }
            } catch (Exception e) {
                status = CheckStatus.DOWN;
                message = "Error de conexión: " + (e.getMessage() != null ? e.getMessage() : "Timeout/Inalcanzable");
            }

            long totalDuration = System.currentTimeMillis() - startTime;

            // 1. Guardar chequeo en BD con los DTOs y Enums reales
            systemCheckService.createCheck(new CreateCheckRequest(moduleName, status, message));

            // 2. Log de evento
            systemEventLogger.info("INTEGRATION_CHECK", 200, "Módulo " + moduleName + " estado: " + status + " (" + totalDuration + "ms)");

            statusList.add(new ModuleStatus(moduleName, url, status.name(), totalDuration, message));
        }

        return statusList;
    }

    public record ModuleStatus(
            String module,
            String url,
            String status,
            long responseTimeMs,
            String details
    ) {}
}