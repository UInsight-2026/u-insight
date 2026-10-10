package gt.edu.uinsight.student.exception;

import java.time.LocalDateTime;
import java.util.List;

public record StudentApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details,
        String traceId
) {
}