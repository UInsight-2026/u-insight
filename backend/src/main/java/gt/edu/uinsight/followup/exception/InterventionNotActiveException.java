// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.exception;

/**
 * RN-5: se lanza al intentar registrar un follow-up sobre una intervención que
 * no está en estado PLANNED o IN_PROGRESS.
 * Mapeada a HTTP 409 por {@link FollowUpExceptionHandler}.
 */
public class InterventionNotActiveException extends RuntimeException {

    public InterventionNotActiveException(Long interventionId) {
        super("La intervención " + interventionId + " no está en estado PLANNED o IN_PROGRESS (RN-5)");
    }
}
