package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.report.mock.model.MockAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Lectura de alertas para los reportes de C5.
 *
 * TEMPORAL semana 4 — reemplazar por la API de C3 cuando exista. Mientras tanto se
 * consulta la tabla `alert` con JdbcTemplate, con las mismas columnas que usa la
 * célula C4 en AlertValidationJdbcAdapter: id, section_id, status y created_at.
 */
@Component
@ConditionalOnProperty(name = "c5.alert-source", havingValue = "jdbc")
public class AlertJdbcQueryPort implements AlertQueryPort {

    private static final Logger log = LoggerFactory.getLogger(AlertJdbcQueryPort.class);
    private static final String COLUMNAS = "id, section_id, status, created_at";

    private final JdbcTemplate jdbcTemplate;

    public AlertJdbcQueryPort(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MockAlert> findAll() {
        try {
            return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM alert", this::aAlerta);
        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source=C3 message={}", ex.getMessage());
            return List.of();
        }
    }

    @Override
    public List<MockAlert> findBySectionId(Long sectionId) {
        try {
            return jdbcTemplate.query(
                    "SELECT " + COLUMNAS + " FROM alert WHERE section_id = ?",
                    this::aAlerta, sectionId);
        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source=C3 sectionId={} message={}", sectionId, ex.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM alert", Integer.class);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private MockAlert aAlerta(ResultSet rs, int fila) throws SQLException {
        var creadaEn = rs.getTimestamp("created_at");
        return new MockAlert(
                rs.getLong("id"),
                rs.getLong("section_id"),
                null, // sectionName
                null, // courseId
                null, // courseCode
                null, // teacherCode
                null, // period
                null, // type
                null, // riskLevel: lo calcula B7 por sección
                rs.getString("status"),
                null, // title
                creadaEn == null ? null : creadaEn.toInstant().toString());
    }
}