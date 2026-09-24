package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.dto.response.ReadinessResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.service.ReadinessService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadinessControllerTest {

    @Test
    void shouldReturn200WhenEverythingIsReady() {

        ReadinessService readinessService = mock(ReadinessService.class);
        ReadinessController controller = new ReadinessController(readinessService);

        ReadinessResponse response = new ReadinessResponse(
                true,
                CheckStatus.UP,
                CheckStatus.UP,
                CheckStatus.UP
        );

        when(readinessService.checkReadiness()).thenReturn(response);

        ResponseEntity<ReadinessResponse> result = controller.readiness();

        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody().ready());
        assertEquals(CheckStatus.UP, result.getBody().database());
    }

    @Test
    void shouldReturn503WhenDatabaseIsDown() {

        ReadinessService readinessService = mock(ReadinessService.class);
        ReadinessController controller = new ReadinessController(readinessService);

        ReadinessResponse response = new ReadinessResponse(
                false,
                CheckStatus.DOWN,
                CheckStatus.UP,
                CheckStatus.UP
        );

        when(readinessService.checkReadiness()).thenReturn(response);

        ResponseEntity<ReadinessResponse> result = controller.readiness();

        assertEquals(503, result.getStatusCode().value());
        assertTrue(!result.getBody().ready());
        assertEquals(CheckStatus.DOWN, result.getBody().database());
    }
}