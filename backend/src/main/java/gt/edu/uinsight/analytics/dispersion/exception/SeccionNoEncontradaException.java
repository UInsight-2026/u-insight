package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando el id de sección solicitado para el cálculo de
 * dispersión no existe en la base de datos.
 *
 * El DispersionExceptionHandler la traduce a HTTP 404 Not Found.
 */
public class SeccionNoEncontradaException extends RuntimeException {

    public SeccionNoEncontradaException(Long sectionId) {
        super("No se encontró la sección con id " + sectionId + ".");
    }
}
