package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.config.IntegrationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class IntegrationStatusServiceTest {

    @Mock
    private SystemCheckService systemCheckService;

    @Mock
    private gt.edu.uinsight.system.logging.SystemEventLogger systemEventLogger;

    private IntegrationProperties properties;
    private MockRestServiceServer mockServer;
    private IntegrationStatusService integrationStatusService;

    @BeforeEach
    void setUp() {
        properties = new IntegrationProperties();
        properties.setDegradedThresholdMs(100);

        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();

        integrationStatusService = new IntegrationStatusService(
                properties,
                systemCheckService,
                systemEventLogger,
                builder
        );
    }

    @Test
    void testModuleUp() {
        properties.setEndpoints(Map.of("A1", "http://localhost:8081/api/v1/academic-periods"));

        mockServer.expect(requestTo("http://localhost:8081/api/v1/academic-periods"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        List<IntegrationStatusService.ModuleStatus> result = integrationStatusService.getIntegrationStatus();

        assertEquals(1, result.size());
        assertEquals("UP", result.get(0).status());
    }

    @Test
    void testModuleDown() {
        properties.setEndpoints(Map.of("A2", "http://localhost:8082/api/v1/teachers"));

        mockServer.expect(requestTo("http://localhost:8082/api/v1/teachers"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        List<IntegrationStatusService.ModuleStatus> result = integrationStatusService.getIntegrationStatus();

        assertEquals(1, result.size());
        assertEquals("DOWN", result.get(0).status());
    }

    @Test
    void testModuleDegraded() {
        properties.setEndpoints(Map.of("A5", "http://localhost:8085/api/v1/evaluations"));

        mockServer.expect(requestTo("http://localhost:8085/api/v1/evaluations"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(request -> {
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return withSuccess("{}", MediaType.APPLICATION_JSON).createResponse(request);
                });

        List<IntegrationStatusService.ModuleStatus> result = integrationStatusService.getIntegrationStatus();

        assertEquals(1, result.size());
        assertEquals("DEGRADED", result.get(0).status());
    }
}