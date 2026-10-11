package gt.edu.uinsight.analytics.summary.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import gt.edu.uinsight.analytics.summary.controller.SummaryController;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice(assignableTypes = SummaryController.class)
public class SummaryExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SummaryExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SummaryErrorResponse> handleUnexpectedError(
            Exception exception, HttpServletRequest request) {
        log.error("Error inesperado al generar el resumen analítico", exception);

        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        SummaryErrorResponse response = new SummaryErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "Error inesperado en el servidor",
                List.of(),
                traceId,
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
