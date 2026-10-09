<<<<<<< HEAD
package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando la cantidad de calificaciones (DispersionGrade) disponibles
 * para una sección o curso no es suficiente para calcular las
 * medidas de dispersión (mínimo 2 valores).
 */
public class DispersionDatosInsuficientesException extends RuntimeException {
=======

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
>>>>>>> develop

    public DispersionDatosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
