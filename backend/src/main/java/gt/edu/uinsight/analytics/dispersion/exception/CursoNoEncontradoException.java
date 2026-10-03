package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando el id de curso solicitado para el cálculo de
 * dispersión no existe en la base de datos.
 *
 * El DispersionExceptionHandler la traduce a HTTP 404 Not Found.
 */
public class CursoNoEncontradoException extends RuntimeException {

    public CursoNoEncontradoException(Long courseId) {
        super("No se encontró el curso con id " + courseId + ".");
    }
}
