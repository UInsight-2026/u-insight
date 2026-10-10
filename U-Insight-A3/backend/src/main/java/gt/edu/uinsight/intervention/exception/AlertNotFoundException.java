// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.exception;

/**
 * RN-1: se lanza cuando no existe una alerta con el id indicado.
 * Mapeada a HTTP 404 por {@link InterventionExceptionHandler}.
 */
public class AlertNotFoundException extends RuntimeException {

    public AlertNotFoundException(Long alertId) {
        super("No existe una alerta con id " + alertId);
    }
}
