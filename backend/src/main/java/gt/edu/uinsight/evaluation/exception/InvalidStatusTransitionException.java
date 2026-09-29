// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

/**
 * RN6: se lanza cuando se solicita una transición de estado no permitida
 * (p. ej. CLOSED -> ACTIVE o ACTIVE -> DRAFT). Un estado inexistente se rechaza antes con 400.
 * Mapeada a HTTP 409 por {@link EvaluationExceptionHandler}.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
