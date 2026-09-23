// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.exception;

/**
 * RN-2: se lanza al intentar crear una intervención sobre una alerta en estado
 * RESOLVED o DISMISSED. Mapeada a HTTP 409 por {@link InterventionExceptionHandler}.
 */
public class AlertNotActiveException extends RuntimeException {

    public AlertNotActiveException(Long alertId) {
        super("La alerta " + alertId + " no está activa (RESOLVED o DISMISSED)");
    }
}
