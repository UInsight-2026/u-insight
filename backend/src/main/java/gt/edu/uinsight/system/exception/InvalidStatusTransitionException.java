package gt.edu.uinsight.system.exception;

import gt.edu.uinsight.system.entity.CheckStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(CheckStatus from, CheckStatus to) {
        super("Invalid status transition from " + from + " to " + to
                + ": a check in DOWN must go through DEGRADED first");
    }
}
