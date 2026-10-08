// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy

package gt.edu.uinsight.intervention.service;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@Profile("!local")
public class AlertValidationJdbcAdapter implements AlertValidationPort {

    private static final List<String> INACTIVE_STATUSES =
            List.of("RESOLVED", "DISMISSED");

    private final JdbcTemplate jdbcTemplate;

    public AlertValidationJdbcAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean exists(Long alertId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM alert WHERE id = ?",
                Integer.class,
                alertId
        );
        return count != null && count > 0;
    }

    @Override
    public boolean isActive(Long alertId) {
        List<String> statuses = jdbcTemplate.query(
                "SELECT status FROM alert WHERE id = ?",
                (rs, rowNum) -> rs.getString("status"),
                alertId
        );

        if (statuses.isEmpty() || statuses.get(0) == null) {
            return false;
        }

        String status = statuses.get(0).toUpperCase(Locale.ROOT);
        return !INACTIVE_STATUSES.contains(status);
    }
}
