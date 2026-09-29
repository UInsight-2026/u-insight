// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

import java.math.BigDecimal;

/**
 * RN4: se lanza cuando la suma de ponderaciones vigentes de una sección,
 * más la nueva ponderación, supera el límite permitido (100%).
 * Mapeada a HTTP 422 por {@link EvaluationExceptionHandler}.
 */
public class WeightLimitExceededException extends RuntimeException {

    public WeightLimitExceededException(Long sectionId, BigDecimal currentTotal, BigDecimal limit) {
        super("La sección " + sectionId + " ya tiene " + currentTotal
                + "% de ponderación asignada; no se puede superar el límite de " + limit + "%");
    }
}
