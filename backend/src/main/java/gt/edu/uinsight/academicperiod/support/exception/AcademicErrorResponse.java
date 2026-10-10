package gt.edu.uinsight.academicperiod.support.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato de error comun del proyecto (seccion 10.1), usado por los endpoints de A1.
 */
public record AcademicErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details,
        String traceId
) {
}
