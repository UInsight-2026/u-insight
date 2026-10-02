package gt.edu.uinsight.analytics.trend.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Formato de error estándar para los endpoints de tendencias. */
public record TrendErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details,
        String traceId) {
}
