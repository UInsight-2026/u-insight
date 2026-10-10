// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.api;

import java.math.BigDecimal;

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
        String status
) {
    /** Solo se registran calificaciones sobre evaluaciones ACTIVE. */
    public boolean isGradable() {
        return "ACTIVE".equals(status);
    }
}
