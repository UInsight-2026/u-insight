// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

/**
 * RN5: se lanza al intentar modificar (PUT) una evaluación cuyo estado es CLOSED.
 * Mapeada a HTTP 409 por {@link EvaluationExceptionHandler}.
 */
public class EvaluationNotEditableException extends RuntimeException {

    public EvaluationNotEditableException(Long evaluationId) {
        super("La evaluación " + evaluationId + " está CLOSED y no puede modificarse");
    }
}
