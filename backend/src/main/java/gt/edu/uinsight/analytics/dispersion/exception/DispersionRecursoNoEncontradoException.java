package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando no existe la sección o curso
 * solicitado para el análisis de dispersión.
 */
public class DispersionRecursoNoEncontradoException
        extends RuntimeException {

    public DispersionRecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
