package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.entity.CheckStatus;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseHealthIndicator {

    private static final int VALIDATION_TIMEOUT_SECONDS = 2;

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CheckStatus check() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(VALIDATION_TIMEOUT_SECONDS)
                    ? CheckStatus.UP
                    : CheckStatus.DOWN;
        } catch (SQLException exception) {
            return CheckStatus.DOWN;
        }
    }
}
