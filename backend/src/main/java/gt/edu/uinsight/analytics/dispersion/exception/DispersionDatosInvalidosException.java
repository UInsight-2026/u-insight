package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando la lista de calificaciones (DispersionGrade) contiene
 * registros con valores nulos o inválidos (score nulo).
 */
public class DispersionDatosInvalidosException extends RuntimeException {

    public DispersionDatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
