package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando la lista de calificaciones (Grade) contiene
 * registros con valores nulos o inválidos (score nulo).
 */
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
