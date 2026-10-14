// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.exception;

import gt.edu.uinsight.intervention.entity.InterventionStatus;

/**
 * Se lanza cuando se solicita una transición de estado no permitida (RN-4).
 * Mapeada a HTTP 409 por {@link InterventionExceptionHandler}.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(InterventionStatus from, InterventionStatus to) {
        super("No se permite la transición de " + from + " a " + to);
    }
}
