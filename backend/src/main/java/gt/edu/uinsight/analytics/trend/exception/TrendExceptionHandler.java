package gt.edu.uinsight.analytics.trend.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import gt.edu.uinsight.analytics.trend.controller.TrendController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/** Manejo de errores limitado al controlador B4 para evitar conflictos entre células. */
@RestControllerAdvice(assignableTypes = TrendController.class)
public class TrendExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrendExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<TrendErrorResponse> handleNotFound(
            EntityNotFoundException exception, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TrendErrorResponse> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<TrendErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .sorted()
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details, request);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<TrendErrorResponse> handleBadRequest(
            Exception exception, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", request);
    }

    @ExceptionHandler(TrendCalculationException.class)
    public ResponseEntity<TrendErrorResponse> handleCalculation(
            TrendCalculationException exception, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "TREND_CALCULATION_ERROR",
                exception.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TrendErrorResponse> handleUnexpected(
            Exception exception, HttpServletRequest request) {
        LOGGER.error("Unexpected error while processing trend request", exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred", request);
    }

    private ResponseEntity<TrendErrorResponse> build(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        return build(status, error, message, List.of(), request);
    }

    private ResponseEntity<TrendErrorResponse> build(
            HttpStatus status, String error, String message, List<String> details,
            HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        TrendErrorResponse response = new TrendErrorResponse(
                LocalDateTime.now(), status.value(), error, message, details, traceId);
        return ResponseEntity.status(status).body(response);
    }
}
