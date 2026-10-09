
package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando la cantidad de calificaciones disponibles
 * para una sección o curso es insuficiente para calcular
 * las medidas de dispersión.
 *
 * Se requieren al menos dos calificaciones válidas.
 */
public class DispersionDatosInsuficientesException
        extends RuntimeException {

    public DispersionDatosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
