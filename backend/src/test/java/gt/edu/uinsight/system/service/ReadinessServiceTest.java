package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.response.ReadinessResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadinessServiceTest {

    @Test
    void shouldNotBeReadyWhenDatabaseIsDown() {

        DatabaseHealthIndicator databaseHealthIndicator =
                mock(DatabaseHealthIndicator.class);

        when(databaseHealthIndicator.check()).thenReturn(CheckStatus.DOWN);

        ReadinessService service =
                new ReadinessService(databaseHealthIndicator);

        ReadinessResponse response = service.checkReadiness();

        assertFalse(response.ready());
        assertEquals(CheckStatus.DOWN, response.database());
        assertEquals(CheckStatus.UP, response.configuration());
        assertEquals(CheckStatus.UP, response.criticalServices());
    }
}