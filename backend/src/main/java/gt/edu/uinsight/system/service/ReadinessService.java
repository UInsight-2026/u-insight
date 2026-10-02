package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.response.ReadinessResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.logging.SystemEventLogger;
import gt.edu.uinsight.system.repository.SystemCheckLogRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReadinessService {

    private final DatabaseHealthIndicator databaseHealthIndicator;
    private final Environment environment;
    private final SystemCheckLogRepository systemCheckLogRepository;
    private final IntegrationStatusService integrationStatusService;
    private final SystemEventLogger systemEventLogger;

    public ReadinessService(
            DatabaseHealthIndicator databaseHealthIndicator,
            Environment environment,
            SystemCheckLogRepository systemCheckLogRepository,
            IntegrationStatusService integrationStatusService,
            SystemEventLogger systemEventLogger) {

        this.databaseHealthIndicator = databaseHealthIndicator;
        this.environment = environment;
        this.systemCheckLogRepository = systemCheckLogRepository;
        this.integrationStatusService = integrationStatusService;
        this.systemEventLogger = systemEventLogger;
    }

    public ReadinessResponse checkReadiness() {

        CheckStatus database = databaseHealthIndicator.check();

        List<String> configurationDetails = checkConfiguration();
        CheckStatus configuration = configurationDetails.stream()
                .anyMatch(detail -> detail.endsWith(": MISSING"))
                ? CheckStatus.DOWN
                : CheckStatus.UP;

        List<String> criticalServicesDetails = checkCriticalServices();
        CheckStatus criticalServices = criticalServicesDetails.stream()
                .anyMatch(detail -> detail.endsWith(": DOWN"))
                ? CheckStatus.DOWN
                : CheckStatus.UP;

        boolean ready = database == CheckStatus.UP
                && configuration == CheckStatus.UP
                && criticalServices == CheckStatus.UP;

        ReadinessResponse response = new ReadinessResponse(
                ready,
                database,
                configuration,
                criticalServices,
                configurationDetails,
                criticalServicesDetails
        );

        if (!ready) {
            systemEventLogger.warn(
                    "READINESS_CHECK",
                    503,
                    "Readiness check failed: " + buildFailureMessage(
                            database,
                            configuration,
                            criticalServices
                    )
            );
        }

        return response;
    }

    private List<String> checkConfiguration() {

        List<String> details = new ArrayList<>();

        checkProperty(
                "spring.datasource.url",
                details
        );

        checkProperty(
                "spring.datasource.username",
                details
        );

        checkProperty(
                "server.port",
                details
        );

        checkProperty(
                "springdoc.swagger-ui.path",
                details
        );

        return details;
    }

    private void checkProperty(String propertyName, List<String> details) {

        String value = environment.getProperty(propertyName);

        if (value == null || value.isBlank()) {
            details.add(propertyName + ": MISSING");
        } else {
            details.add(propertyName + ": OK");
        }
    }

    private List<String> checkCriticalServices() {

        List<String> details = new ArrayList<>();

        try {
            systemCheckLogRepository.count();
            details.add("SystemCheckLogRepository: OK");
        } catch (Exception exception) {
            details.add("SystemCheckLogRepository: DOWN");
        }

        try {
            if (integrationStatusService != null) {
                details.add("IntegrationStatusService: OK");
            } else {
                details.add("IntegrationStatusService: DOWN");
            }
        } catch (Exception exception) {
            details.add("IntegrationStatusService: DOWN");
        }

        return details;
    }

    private String buildFailureMessage(
            CheckStatus database,
            CheckStatus configuration,
            CheckStatus criticalServices) {

        List<String> failures = new ArrayList<>();

        if (database == CheckStatus.DOWN) {
            failures.add("database");
        }

        if (configuration == CheckStatus.DOWN) {
            failures.add("configuration");
        }

        if (criticalServices == CheckStatus.DOWN) {
            failures.add("criticalServices");
        }

        return String.join(", ", failures);
    }
}