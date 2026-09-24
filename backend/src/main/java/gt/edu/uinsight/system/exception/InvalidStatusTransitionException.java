package gt.edu.uinsight.system.exception;

import gt.edu.uinsight.system.entity.CheckStatus;

/**
 * Regla de negocio: una comprobacion DOWN no puede pasar directamente a UP.
 * El codigo HTTP 409 lo asigna SystemExceptionHandler, para que el cuerpo
 * conserve el formato de error de la seccion 10.1.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(CheckStatus from, CheckStatus to) {
        super("Invalid status transition from " + from + " to " + to
                + ": a check in DOWN must go through DEGRADED first");
    }
}
