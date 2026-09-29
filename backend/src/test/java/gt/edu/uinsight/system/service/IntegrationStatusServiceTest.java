package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.response.IntegrationStatusResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;

class IntegrationStatusServiceTest {

    private IntegrationStatusService service;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();

        mockServer = MockRestServiceServer.bindTo(builder).build();

        service = new IntegrationStatusService(builder);
    }

    @Test
    void todosLosModulosDebenEstarUP() {

        mockServer.expect(requestTo("http://localhost:8080/api/v1/teachers"))
                .andRespond(withSuccess());

        mockServer.expect(requestTo("http://localhost:8080/api/v1/evaluations"))
                .andRespond(withSuccess());

        mockServer.expect(requestTo("http://localhost:8080/api/v1/alerts/1/interventions"))
                .andRespond(withSuccess());

        List<IntegrationStatusResponse> result =
                service.getIntegrationStatus();

        assertEquals(3, result.size());

        assertTrue(result.stream()
                .allMatch(status -> status.status() == CheckStatus.UP));

        mockServer.verify();
    }

    @Test
    void unModuloDOWNNoDebeLanzarExcepcion() {

        mockServer.expect(requestTo("http://localhost:8080/api/v1/teachers"))
                .andRespond(withSuccess());

        mockServer.expect(requestTo("http://localhost:8080/api/v1/evaluations"))
                .andRespond(withServerError());

        mockServer.expect(requestTo("http://localhost:8080/api/v1/alerts/1/interventions"))
                .andRespond(withSuccess());

        List<IntegrationStatusResponse> result =
                assertDoesNotThrow(() -> service.getIntegrationStatus());

        assertEquals(3, result.size());

        assertEquals(CheckStatus.UP, result.get(0).status());
        assertEquals(CheckStatus.DOWN, result.get(1).status());
        assertEquals(CheckStatus.UP, result.get(2).status());

        mockServer.verify();
    }
}