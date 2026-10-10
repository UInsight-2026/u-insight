package gt.edu.uinsight.analytics.dispersion.exception;

<<<<<<< HEAD
public class DispersionDatosInsuficientesException
        extends RuntimeException {
=======
/**
 * Se lanza cuando la cantidad de calificaciones (DispersionGrade) disponibles
 * para una sección o curso no es suficiente para calcular las
 * medidas de dispersión (mínimo 2 valores).
 */
public class DispersionDatosInsuficientesException extends RuntimeException {
>>>>>>> 1ee7dac03082d19815951d48f50eb659e87dae52

    public DispersionDatosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
