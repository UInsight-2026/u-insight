package gt.edu.uinsight.report.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Manejador de excepciones de la Celula C5.
 *
 * Se limita al paquete de C5 con basePackages para no alterar las
 * respuestas de error de las demas celulas, y NO se llama
 * GlobalExceptionHandler porque B5 ya tiene una clase con ese nombre
 * (Spring no admite dos componentes con el mismo nombre simple).
 */
@RestControllerAdvice(basePackages = "gt.edu.uinsight.report")
public class ReportExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ReportExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        String traceId = newTraceId();
        log.warn("RESOURCE_NOT_FOUND traceId={} message={}", traceId, ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                Collections.emptyList(),
                traceId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<ApiError> handleInvalidFilter(InvalidFilterException ex) {
        String traceId = newTraceId();
        log.warn("VALIDATION_ERROR traceId={} message={}", traceId, ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                ex.getMessage(),
                List.of(ex.getMessage()),
                traceId);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String traceId = newTraceId();
        String message = "El parametro '" + ex.getName() + "' tiene un formato invalido.";
        log.warn("VALIDATION_ERROR traceId={} message={}", traceId, message);
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                message,
                List.of(message),
                traceId);
        return ResponseEntity.badRequest().body(error);
    }

    private String newTraceId() {
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
