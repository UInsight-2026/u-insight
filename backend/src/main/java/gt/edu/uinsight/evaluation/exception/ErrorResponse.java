// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato de error estándar del proyecto:
 * {@code { timestamp, status, error, message, details, traceId }}.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details,
        String traceId
) {
}
