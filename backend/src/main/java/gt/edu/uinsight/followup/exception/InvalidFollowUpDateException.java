// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.exception;

/**
 * RN-6: se lanza cuando followUpDate es anterior al startDate de la
 * intervención asociada. Mapeada a HTTP 409 por {@link FollowUpExceptionHandler}.
 */
public class InvalidFollowUpDateException extends RuntimeException {

    public InvalidFollowUpDateException(String message) {
        super(message);
    }
}
