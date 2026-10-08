// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.exception;

import gt.edu.uinsight.intervention.controller.InterventionController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Manejador de errores acotado a InterventionController (assignableTypes)
 * para no chocar con un RestControllerAdvice global de otra célula.
 */
@RestControllerAdvice(assignableTypes = InterventionController.class)
public class InterventionExceptionHandler {

    // Logger para mostrar los errores en la terminal
    private static final Logger log =
            LoggerFactory.getLogger(InterventionExceptionHandler.class);

    @ExceptionHandler(AlertNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAlertNotFound(
            AlertNotFoundException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.NOT_FOUND,
                "ALERT_NOT_FOUND",
                ex.getMessage(),
                null,
                request
        );
    }

    @ExceptionHandler(InterventionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInterventionNotFound(
            InterventionNotFoundException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.NOT_FOUND,
                "INTERVENTION_NOT_FOUND",
                ex.getMessage(),
                null,
                request
        );
    }

    @ExceptionHandler(AlertNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleAlertNotActive(
            AlertNotActiveException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.CONFLICT,
                "ALERT_NOT_ACTIVE",
                ex.getMessage(),
                null,
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Los datos enviados no son válidos",
                details,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {

        // Imprime la causa del error y su stack trace en la terminal
        log.error(
                "Error inesperado en la solicitud: {}",
                request.getRequestURI(),
                ex
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Ocurrió un error inesperado",
                null,
                request
        );
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String error,
            String message,
            List<String> details,
            HttpServletRequest request) {

        String traceId = request.getHeader("X-Trace-Id");

        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                details,
                traceId
        );

        return ResponseEntity.status(status).body(body);
    }
}