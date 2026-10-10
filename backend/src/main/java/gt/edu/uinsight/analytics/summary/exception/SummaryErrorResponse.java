package gt.edu.uinsight.analytics.summary.exception;

import java.time.LocalDateTime;
import java.util.List;

public record SummaryErrorResponse(
        int status,
        String error,
        String message,
        List<String> details,
        String traceId,
        LocalDateTime timestamp) {
}
