package gt.edu.uinsight.intervention.exception;

/**
 * Se lanza cuando no existe una intervención con el id indicado.
 * Mapeada a HTTP 404 por {@link InterventionExceptionHandler}.
 */
public class InterventionNotFoundException extends RuntimeException {

    public InterventionNotFoundException(Long id) {
        super("No existe una intervención con id " + id);
    }
}
