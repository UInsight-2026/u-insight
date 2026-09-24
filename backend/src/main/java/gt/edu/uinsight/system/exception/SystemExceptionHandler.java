package gt.edu.uinsight.system.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import gt.edu.uinsight.system.logging.SystemEventLogger;

@RestControllerAdvice(basePackages = "gt.edu.uinsight.system")
public class SystemExceptionHandler {

    private final SystemEventLogger eventLogger;

    public SystemExceptionHandler(SystemEventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    @ExceptionHandler(CheckNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCheckNotFound(
            CheckNotFoundException exception) {

        return buildError(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                exception.getMessage(),
                List.of(),
                "RESOURCE_NOT_FOUND"
        );
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatusTransition(
            InvalidStatusTransitionException exception) {

        return buildError(
                HttpStatus.CONFLICT,
                "CONFLICT",
                exception.getMessage(),
                List.of(),
                "BUSINESS_RULE_REJECTED"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception) {

        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildError(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                details,
                "VALIDATION_REJECTED"
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(
            HttpMessageNotReadableException exception) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                "Malformed or unreadable request body",
                List.of(),
                "VALIDATION_REJECTED"
        );
    }

    /**
     * La capa de servicio lanza IllegalArgumentException cuando rechaza una regla
     * de negocio, por eso el evento es BUSINESS_RULE_REJECTED y no un error tecnico.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException exception) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                exception.getMessage(),
                List.of(),
                "BUSINESS_RULE_REJECTED"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                "Invalid value for parameter '" + exception.getName() + "'",
                List.of(),
                "VALIDATION_REJECTED"
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            NoResourceFoundException exception) {

        return buildError(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "Resource not found",
                List.of(),
                "RESOURCE_NOT_FOUND"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalError(
            Exception exception) {

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                List.of(),
                "UNEXPECTED_ERROR"
        );
    }

    /**
     * Arma el cuerpo de error de la seccion 10.1 y registra el evento de log de la
     * seccion 10.2 con el mismo traceId, para que la respuesta y la linea de consola
     * se puedan emparejar.
     */
    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status,
            String error,
            String message,
            List<String> details,
            String operation) {

        String traceId = eventLogger.currentTraceId();
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        String safeMessage = message != null ? message : "";

        if (status.is5xxServerError()) {
            eventLogger.error(operation, status.value(), safeMessage);
        } else {
            eventLogger.warn(operation, status.value(), safeMessage);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", safeMessage);
        response.put("details", details);
        response.put("traceId", traceId);

        return ResponseEntity.status(status).body(response);
    }
}
