<<<<<<< HEAD
package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando la lista de calificaciones (DispersionGrade) contiene
 * registros con valores nulos o inválidos (score nulo).
 */
public class DispersionDatosInvalidosException extends RuntimeException {
=======

package gt.edu.uinsight.analytics.dispersion.exception;

/**
 * Se lanza cuando se detectan datos inválidos durante
 * el análisis de dispersión.
 *
 * Incluye calificaciones nulas, negativas o superiores
 *  *al máximo permitido de la evaluación.
 */
public class DispersionDatosInvalidosException
        extends RuntimeException {
>>>>>>> develop

    public DispersionDatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
