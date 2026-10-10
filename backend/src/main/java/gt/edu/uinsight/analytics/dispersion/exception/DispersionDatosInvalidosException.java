package gt.edu.uinsight.analytics.dispersion.exception;

<<<<<<< HEAD
public class DispersionDatosInvalidosException
        extends RuntimeException {
=======
/**
 * Se lanza cuando la lista de calificaciones (DispersionGrade) contiene
 * registros con valores nulos o inválidos (score nulo).
 */
public class DispersionDatosInvalidosException extends RuntimeException {
>>>>>>> 1ee7dac03082d19815951d48f50eb659e87dae52

    public DispersionDatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
