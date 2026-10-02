package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.response.ReadinessResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import org.springframework.stereotype.Service;

@Service
public class ReadinessService {

    private final DatabaseHealthIndicator databaseHealthIndicator;

    public ReadinessService(DatabaseHealthIndicator databaseHealthIndicator) {
        this.databaseHealthIndicator = databaseHealthIndicator;
    }

    public ReadinessResponse checkReadiness() {

       CheckStatus database = databaseHealthIndicator.check();

        CheckStatus configuration = CheckStatus.UP;

        CheckStatus criticalServices = CheckStatus.UP;

        boolean ready = database == CheckStatus.UP
                && configuration == CheckStatus.UP
                && criticalServices == CheckStatus.UP;

        return new ReadinessResponse(
                ready,
                database,
                configuration,
                criticalServices
        );
    }
}