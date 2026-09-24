package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.response.IntegrationStatusResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntegrationStatusService {

    private final RestClient restClient;

    public IntegrationStatusService() {
        this(RestClient.builder());
    }

    IntegrationStatusService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public List<IntegrationStatusResponse> getIntegrationStatus() {
        List<IntegrationStatusResponse> results = new ArrayList<>();

        results.add(checkModule("teachers", "/api/v1/teachers"));
        results.add(checkModule("evaluations", "/api/v1/evaluations"));
        results.add(checkModule("interventions", "/api/v1/alerts/1/interventions"));

        return results;
    }

    private IntegrationStatusResponse checkModule(String name, String endpoint) {
        long start = System.currentTimeMillis();

        try {
            restClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .toBodilessEntity();

            long responseTime = System.currentTimeMillis() - start;

            return new IntegrationStatusResponse(
                    name,
                    CheckStatus.UP,
                    responseTime
            );

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - start;

            return new IntegrationStatusResponse(
                    name,
                    CheckStatus.DOWN,
                    responseTime
            );
        }
    }
}