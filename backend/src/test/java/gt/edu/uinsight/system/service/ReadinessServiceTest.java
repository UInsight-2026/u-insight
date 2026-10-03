package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.response.ReadinessResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.logging.SystemEventLogger;
import gt.edu.uinsight.system.repository.SystemCheckLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadinessServiceTest {

    @Test
    void shouldReturnReadyWhenEverythingIsAvailable() {

        DatabaseHealthIndicator databaseHealthIndicator =
                mock(DatabaseHealthIndicator.class);

        Environment environment = mock(Environment.class);

        SystemCheckLogRepository repository =
                mock(SystemCheckLogRepository.class);

        IntegrationStatusService integrationStatusService =
                mock(IntegrationStatusService.class);

        SystemEventLogger systemEventLogger =
                mock(SystemEventLogger.class);

        when(databaseHealthIndicator.check()).thenReturn(CheckStatus.UP);

        when(environment.getProperty("spring.datasource.url"))
                .thenReturn("jdbc:mysql://localhost:3307/uinsight");

        when(environment.getProperty("spring.datasource.username"))
                .thenReturn("uinsight");

        when(environment.getProperty("server.port"))
                .thenReturn("8080");

        when(environment.getProperty("springdoc.swagger-ui.path"))
                .thenReturn("/swagger-ui.html");

        when(repository.count()).thenReturn(0L);

        ReadinessService service = new ReadinessService(
                databaseHealthIndicator,
                environment,
                repository,
                integrationStatusService,
                systemEventLogger
        );

        ReadinessResponse response = service.checkReadiness();

        assertTrue(response.ready());
        assertEquals(CheckStatus.UP, response.database());
        assertEquals(CheckStatus.UP, response.configuration());
        assertEquals(CheckStatus.UP, response.criticalServices());
    }

    @Test
    void shouldReturnDownConfigurationWhenCriticalPropertyIsMissing() {

        DatabaseHealthIndicator databaseHealthIndicator =
                mock(DatabaseHealthIndicator.class);

        Environment environment = mock(Environment.class);

        SystemCheckLogRepository repository =
                mock(SystemCheckLogRepository.class);

        IntegrationStatusService integrationStatusService =
                mock(IntegrationStatusService.class);

        SystemEventLogger systemEventLogger =
                mock(SystemEventLogger.class);

        when(databaseHealthIndicator.check()).thenReturn(CheckStatus.UP);

        when(environment.getProperty("spring.datasource.url"))
                .thenReturn("jdbc:mysql://localhost:3307/uinsight");

        when(environment.getProperty("spring.datasource.username"))
                .thenReturn(null);

        when(environment.getProperty("server.port"))
                .thenReturn("8080");

        when(environment.getProperty("springdoc.swagger-ui.path"))
                .thenReturn("/swagger-ui.html");

        when(repository.count()).thenReturn(0L);

        ReadinessService service = new ReadinessService(
                databaseHealthIndicator,
                environment,
                repository,
                integrationStatusService,
                systemEventLogger
        );

        ReadinessResponse response = service.checkReadiness();

        assertFalse(response.ready());
        assertEquals(CheckStatus.DOWN, response.configuration());
        assertTrue(response.configurationDetails().stream()
                .anyMatch(detail ->
                        detail.equals("spring.datasource.username: MISSING")));
    }

    @Test
    void shouldReturnDownCriticalServicesWhenRepositoryIsUnavailable() {

        DatabaseHealthIndicator databaseHealthIndicator =
                mock(DatabaseHealthIndicator.class);

        Environment environment = mock(Environment.class);

        SystemCheckLogRepository repository =
                mock(SystemCheckLogRepository.class);

        IntegrationStatusService integrationStatusService =
                mock(IntegrationStatusService.class);

        SystemEventLogger systemEventLogger =
                mock(SystemEventLogger.class);

        when(databaseHealthIndicator.check()).thenReturn(CheckStatus.UP);

        when(environment.getProperty("spring.datasource.url"))
                .thenReturn("jdbc:mysql://localhost:3307/uinsight");

        when(environment.getProperty("spring.datasource.username"))
                .thenReturn("uinsight");

        when(environment.getProperty("server.port"))
                .thenReturn("8080");

        when(environment.getProperty("springdoc.swagger-ui.path"))
                .thenReturn("/swagger-ui.html");

        when(repository.count()).thenThrow(
                new RuntimeException("Database unavailable")
        );

        ReadinessService service = new ReadinessService(
                databaseHealthIndicator,
                environment,
                repository,
                integrationStatusService,
                systemEventLogger
        );

        ReadinessResponse response = service.checkReadiness();

        assertFalse(response.ready());
        assertEquals(CheckStatus.DOWN, response.criticalServices());
        assertTrue(response.criticalServicesDetails().stream()
                .anyMatch(detail ->
                        detail.equals("SystemCheckLogRepository: DOWN")));
    }

    /**
     * Regresion. El perfil de test no declara server.port ni springdoc.swagger-ui.path:
     * src/test/resources/application.properties tapa al de main y no los trae. Exigirlos
     * declarados hacia que readiness devolviera 503 con la aplicacion sana, y la prueba
     * E2E del flujo oficial, que espera 200, no podia pasar nunca.
     */
    @Test
    void shouldStayReadyWhenPropertiesWithFrameworkDefaultAreNotDeclared() {

        DatabaseHealthIndicator databaseHealthIndicator =
                mock(DatabaseHealthIndicator.class);

        Environment environment = mock(Environment.class);

        SystemCheckLogRepository repository =
                mock(SystemCheckLogRepository.class);

        IntegrationStatusService integrationStatusService =
                mock(IntegrationStatusService.class);

        SystemEventLogger systemEventLogger =
                mock(SystemEventLogger.class);

        when(databaseHealthIndicator.check()).thenReturn(CheckStatus.UP);

        when(environment.getProperty("spring.datasource.url"))
                .thenReturn("jdbc:h2:mem:uinsight-test");

        when(environment.getProperty("spring.datasource.username"))
                .thenReturn("sa");

        when(environment.getProperty("server.port"))
                .thenReturn(null);

        when(environment.getProperty("springdoc.swagger-ui.path"))
                .thenReturn(null);

        when(repository.count()).thenReturn(0L);

        ReadinessService service = new ReadinessService(
                databaseHealthIndicator,
                environment,
                repository,
                integrationStatusService,
                systemEventLogger
        );

        ReadinessResponse response = service.checkReadiness();

        assertTrue(response.ready());
        assertEquals(CheckStatus.UP, response.configuration());

        assertTrue(response.configurationDetails().stream()
                .anyMatch(detail ->
                        detail.equals("server.port: OK (valor por defecto 8080)")));

        assertTrue(response.configurationDetails().stream()
                .anyMatch(detail -> detail.equals(
                        "springdoc.swagger-ui.path: OK (valor por defecto /swagger-ui.html)")));
    }

    @Test
    void shouldReturnDownConfigurationWhenServerPortIsOutOfRange() {

        DatabaseHealthIndicator databaseHealthIndicator =
                mock(DatabaseHealthIndicator.class);

        Environment environment = mock(Environment.class);

        SystemCheckLogRepository repository =
                mock(SystemCheckLogRepository.class);

        IntegrationStatusService integrationStatusService =
                mock(IntegrationStatusService.class);

        SystemEventLogger systemEventLogger =
                mock(SystemEventLogger.class);

        when(databaseHealthIndicator.check()).thenReturn(CheckStatus.UP);

        when(environment.getProperty("spring.datasource.url"))
                .thenReturn("jdbc:h2:mem:uinsight-test");

        when(environment.getProperty("spring.datasource.username"))
                .thenReturn("sa");

        when(environment.getProperty("server.port"))
                .thenReturn("99999");

        when(environment.getProperty("springdoc.swagger-ui.path"))
                .thenReturn("/swagger-ui.html");

        when(repository.count()).thenReturn(0L);

        ReadinessService service = new ReadinessService(
                databaseHealthIndicator,
                environment,
                repository,
                integrationStatusService,
                systemEventLogger
        );

        ReadinessResponse response = service.checkReadiness();

        assertFalse(response.ready());
        assertEquals(CheckStatus.DOWN, response.configuration());

        assertTrue(response.configurationDetails().stream()
                .anyMatch(detail -> detail.startsWith("server.port: INVALIDO")));
    }
}
