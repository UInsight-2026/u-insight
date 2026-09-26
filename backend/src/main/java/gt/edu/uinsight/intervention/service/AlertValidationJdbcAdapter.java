// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

// TEMPORAL semana 2 — reemplazar en semana 3 por llamada real a GET /api/v1/alerts/{id}
// de la célula C3 (tarea BKL-07). Mientras esa API no exista, se consulta la tabla
// `alert` directamente con JdbcTemplate. Al reemplazar este adapter, AlertValidationPort
// y sus consumidores (InterventionService) no deberían necesitar cambios.
//
// Asunción sin confirmar con C3: la tabla `alert` tiene columnas `id` y `status`,
// y `status` toma (entre otros) los valores RESOLVED y DISMISSED para "no activa"
// (ver docs/c4-pendientes-coordinacion.md, punto 3).
@Component
public class AlertValidationJdbcAdapter implements AlertValidationPort {

    private static final List<String> INACTIVE_STATUSES = List.of("RESOLVED", "DISMISSED");

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
