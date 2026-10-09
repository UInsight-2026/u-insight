// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

// TEMPORAL — reemplazar cuando la célula A4 exponga su API real
// (GET /api/v1/sections/{id}). Mientras esa API no exista, se consulta la
// tabla `section` directamente con JdbcTemplate. Al reemplazar este adapter,
// SectionValidationPort y sus consumidores (EvaluationServiceImpl) no
// deberían necesitar cambios.
//
// Asunción sin confirmar con A4: la tabla `section` tiene columnas `id` y
// `status`, y `status` = 'ACTIVE' indica una sección activa (el resto de
// valores, p. ej. 'CLOSED' o 'INACTIVE', se tratan como no activa).
//
// Para probar A5 en local antes de que exista el script oficial de A4, usar
// database/scripts/local-dev/stub_section_for_a5_testing.sql
@Component
public class SectionValidationJdbcAdapter implements SectionValidationPort {

    private final JdbcTemplate jdbcTemplate;

    public SectionValidationJdbcAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean exists(Long sectionId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM section WHERE id = ?",
                Integer.class,
                sectionId
        );
        return count != null && count > 0;
    }

    @Override
    public boolean isActive(Long sectionId) {
        List<String> statuses = jdbcTemplate.query(
                "SELECT status FROM section WHERE id = ?",
                (rs, rowNum) -> rs.getString("status"),
                sectionId
        );
        if (statuses.isEmpty() || statuses.get(0) == null) {
            return false;
        }
        return "ACTIVE".equals(statuses.get(0).toUpperCase(Locale.ROOT));
    }
}
