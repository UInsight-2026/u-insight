// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.api.EvaluationSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementación de {@link GradeEvaluationSyncPort} que escribe en la tabla
 * grade_evaluation_ref de A6 (columnas id, section_id, name, maximum_score),
 * sin depender de las clases Java de A6.
 *
 * Si la tabla todavía no existe o falla la escritura, se registra
 * INTEGRATION_ERROR y la operación de A5 continúa: la integración nunca debe
 * romper la gestión de evaluaciones.
 */
@Component
public class GradeEvaluationRefJdbcAdapter implements GradeEvaluationSyncPort {

    private static final Logger log = LoggerFactory.getLogger(GradeEvaluationRefJdbcAdapter.class);

    private static final String UPDATE_SQL =
            "UPDATE grade_evaluation_ref SET section_id = ?, name = ?, maximum_score = ? WHERE id = ?";
    private static final String INSERT_SQL =
            "INSERT INTO grade_evaluation_ref (id, section_id, name, maximum_score) VALUES (?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public GradeEvaluationRefJdbcAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void publish(EvaluationSummary evaluation) {
        try {
            int updated = jdbcTemplate.update(UPDATE_SQL,
                    evaluation.sectionId(), evaluation.name(), evaluation.maximumScore(), evaluation.id());
            if (updated == 0) {
                jdbcTemplate.update(INSERT_SQL,
                        evaluation.id(), evaluation.sectionId(), evaluation.name(), evaluation.maximumScore());
            }
            log.info("EVALUATION_SYNCED_TO_GRADES evaluationId={} sectionId={} maximumScore={} operation={}",
                    evaluation.id(), evaluation.sectionId(), evaluation.maximumScore(),
                    updated == 0 ? "INSERT" : "UPDATE");
        } catch (DataAccessException ex) {
            log.warn("INTEGRATION_ERROR target=A6 table=grade_evaluation_ref evaluationId={} message={}",
                    evaluation.id(), ex.getMostSpecificCause().getMessage());
        }
    }
}
