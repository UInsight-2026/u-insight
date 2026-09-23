// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

/**
 * Se lanza cuando no existe una evaluación con el id indicado.
 * Mapeada a HTTP 404 por {@link EvaluationExceptionHandler}.
 */
public class EvaluationNotFoundException extends RuntimeException {

    public EvaluationNotFoundException(Long id) {
        super("No existe una evaluación con id " + id);
    }
}
