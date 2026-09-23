// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

/**
 * RN1: se lanza cuando no existe una sección con el id indicado.
 * Mapeada a HTTP 422 por {@link EvaluationExceptionHandler}.
 */
public class SectionNotFoundException extends RuntimeException {

    public SectionNotFoundException(Long sectionId) {
        super("No existe una sección con id " + sectionId);
    }
}
