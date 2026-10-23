package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.report.mock.model.MockAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica que el puerto JDBC lee la tabla alert con sus columnas reales
 * (generated_at, no created_at). Antes la consulta fallaba y el error se
 * tragaba devolviendo una lista vacía.
 */
class AlertJdbcQueryPortTest {

    private JdbcTemplate jdbcTemplate;
    private AlertJdbcQueryPort port;

    @BeforeEach
    void setUp() {
        var dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:alert-port-" + System.nanoTime() + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1", "sa", "");
        jdbcTemplate = new JdbcTemplate(dataSource);
        // Mismas columnas que alert/model/Alert.java (las que usa el puerto).
        jdbcTemplate.execute("""
                CREATE TABLE alert (
                    id BIGINT PRIMARY KEY,
                    section_id INT,
                    alert_type VARCHAR(50),
                    severity VARCHAR(20),
                    title VARCHAR(150),
                    status VARCHAR(20) NOT NULL,
                    generated_at TIMESTAMP,
                    risk_level VARCHAR(20)
                )""");
        jdbcTemplate.update("""
                INSERT INTO alert (id, section_id, alert_type, title, status, generated_at) VALUES
                (1, 2, 'PERFORMANCE', 'Promedio bajo', 'NEW', '2026-10-01 10:00:00'),
                (2, 3, 'TREND', 'Tendencia negativa', 'IN_PROGRESS', '2026-10-02 11:30:00'),
                (3, 3, 'DISPERSION', 'Dispersión alta', 'NEW', NULL)""");
        port = new AlertJdbcQueryPort(jdbcTemplate);
    }

    @Test
    void findAllLeeLasAlertasConGeneratedAtTipoYTitulo() {
        List<MockAlert> alertas = port.findAll();

        assertThat(alertas).hasSize(3);
        MockAlert primera = alertas.stream().filter(a -> a.getId() == 1L).findFirst().orElseThrow();
        assertThat(primera.getSectionId()).isEqualTo(2L);
        assertThat(primera.getType()).isEqualTo("PERFORMANCE");
        assertThat(primera.getTitle()).isEqualTo("Promedio bajo");
        assertThat(primera.getStatus()).isEqualTo("NEW");
        assertThat(primera.getGeneratedAt()).isNotNull();
    }

    @Test
    void findAllAceptaGeneratedAtNulo() {
        MockAlert sinFecha = port.findAll().stream().filter(a -> a.getId() == 3L).findFirst().orElseThrow();
        assertThat(sinFecha.getGeneratedAt()).isNull();
    }

    @Test
    void findBySectionIdFiltraPorSeccion() {
        assertThat(port.findBySectionId(3L)).extracting(MockAlert::getId).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void isAvailableEsTrueCuandoExisteLaTabla() {
        assertThat(port.isAvailable()).isTrue();
    }
}
