// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.api;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Vista mínima y estable de una evaluación para otras células (A6 y Sección B).
 * Contiene lo que A6 necesita para validar una calificación: existencia,
 * sección, nota máxima, ponderación y estado.
 */
public record EvaluationSummary(
        Long id,
        Long sectionId,
        String name,
        BigDecimal maximumScore,
        BigDecimal weight,
        String status,
        LocalDate evaluationDate
) {
    public EvaluationSummary(Long id, Long sectionId, String name, BigDecimal maximumScore,
                             BigDecimal weight, String status) {
        this(id, sectionId, name, maximumScore, weight, status, null);
    }

    /** Solo se registran calificaciones sobre evaluaciones ACTIVE. */
    public boolean isGradable() {
        return "ACTIVE".equals(status);
    }
}
