package gt.edu.uinsight.analytics.centraltendency.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAnalyticsExceptionHandler {
private static final Logger log = LoggerFactory.getLogger(GlobalAnalyticsExceptionHandler.class);

    // Error 400: Parámetro inválido o regla rechazada
    @ExceptionHandler(InvalidAnalyticsRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(InvalidAnalyticsRequestException ex) {
        log.warn("ANALYTICS_RULE_REJECTED - {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Error 404: Sección o curso inexistente
    @ExceptionHandler(AnalyticsResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(AnalyticsResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Error 502: Error de integración al consultar dependencias
    @ExceptionHandler(GradeDataIntegrationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrationError(GradeDataIntegrationException ex) {
        log.error("ANALYTICS_INTEGRATION_ERROR - {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    // Error 500: Error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception ex) {
        log.error("Error inesperado en el cálculo de analíticas", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado en el servidor");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse response = new ErrorResponse(status.value(), message, System.currentTimeMillis());
        return new ResponseEntity<>(response, status);
    }
}