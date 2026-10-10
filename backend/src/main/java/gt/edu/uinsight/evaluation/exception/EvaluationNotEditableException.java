// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

/**
 * RN5 / HU3: se lanza al intentar modificar (PUT) una evaluación que no está
 * en DRAFT ni ACTIVE (es decir, CLOSED o CANCELLED).
 * Mapeada a HTTP 409 por {@link EvaluationExceptionHandler}.
 */
public class EvaluationNotEditableException extends RuntimeException {

    public EvaluationNotEditableException(Long evaluationId, String status) {
        super("La evaluación " + evaluationId + " está " + status
                + " y no puede modificarse; solo se editan evaluaciones en DRAFT o ACTIVE");
    }
}
