package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando la cantidad de calificaciones (Grade) disponibles
 * para una sección o curso no es suficiente para calcular las
 * medidas de dispersión (mínimo 2 valores).
 */
public class DatosInsuficientesException extends RuntimeException {

    public DatosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
