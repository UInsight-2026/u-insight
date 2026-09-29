// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

import java.math.BigDecimal;

/**
 * RN4: la suma de ponderaciones vigentes de la sección más la nueva
 * ponderación supera el límite permitido (100%).
 * Mapeada a HTTP 422 por {@link EvaluationExceptionHandler}.
 */
public class WeightLimitExceededException extends RuntimeException {

    public WeightLimitExceededException(Long sectionId, BigDecimal currentTotal, BigDecimal attempted,
                                        BigDecimal limit) {
        super("La suma de ponderaciones de la sección " + sectionId + " sería "
                + currentTotal.add(attempted) + "% (actual " + currentTotal + "% + " + attempted
                + "%) y supera el límite de " + limit + "%");
    }
}
