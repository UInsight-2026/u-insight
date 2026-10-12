// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

/**
 * RN1: se lanza al intentar crear o modificar una evaluación sobre una
 * sección que existe pero no está activa.
 * Mapeada a HTTP 422 por {@link EvaluationExceptionHandler}.
 */
public class SectionNotActiveException extends RuntimeException {

    public SectionNotActiveException(Long sectionId) {
        super("La sección " + sectionId + " no está activa");
    }
}
