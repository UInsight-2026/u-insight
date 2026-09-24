package gt.edu.uinsight.analytics.trend.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import gt.edu.uinsight.analytics.trend.controller.TrendController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/** Manejo de errores limitado al controlador B4 para evitar conflictos entre células. */
@RestControllerAdvice(assignableTypes = TrendController.class)
public class TrendExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<TrendErrorResponse> handleNotFound(
            EntityNotFoundException exception, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(TrendCalculationException.class)
    public ResponseEntity<TrendErrorResponse> handleCalculation(
            TrendCalculationException exception, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "TREND_CALCULATION_ERROR",
                exception.getMessage(), request);
    }

    private ResponseEntity<TrendErrorResponse> build(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        TrendErrorResponse response = new TrendErrorResponse(
                LocalDateTime.now(), status.value(), error, message, List.of(), traceId);
        return ResponseEntity.status(status).body(response);
    }
}
