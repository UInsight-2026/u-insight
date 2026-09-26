// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.exception;

import java.time.LocalDate;

/**
 * RN-6: se lanza cuando {@code followUpDate} es anterior al {@code startDate}
 * de la intervención asociada.
 * Mapeada a HTTP 409 por {@link FollowUpExceptionHandler}.
 */
public class InvalidFollowUpDateException extends RuntimeException {

    public InvalidFollowUpDateException(LocalDate followUpDate, LocalDate startDate) {
        super("followUpDate " + followUpDate + " no puede ser anterior al startDate " + startDate
                + " de la intervención (RN-6)");
    }
}
