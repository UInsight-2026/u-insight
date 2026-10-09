package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando la cantidad de calificaciones (DispersionGrade) disponibles
 * para una sección o curso no es suficiente para calcular las
 * medidas de dispersión (mínimo 2 valores).
 */
public class DispersionDatosInsuficientesException extends RuntimeException {

    public DispersionDatosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
