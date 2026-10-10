package gt.edu.uinsight.analytics.centraltendency.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Acotado a los controllers de B1: un manejador global con catch-all
 * convertiria en 500 los errores de los demas modulos y las rutas inexistentes
 * (confirmado en el informe de pruebas de integracion de A5, 2-oct-2026:
 * capturaba tambien errores de A2, A4 y A6).
 */
@RestControllerAdvice(basePackages = "gt.edu.uinsight.analytics.centraltendency")
public class GlobalAnalyticsExceptionHandler {
private static final Logger log = LoggerFactory.getLogger(GlobalAnalyticsExceptionHandler.class);

    // Error 400: Parámetro inválido o regla rechazada
    @ExceptionHandler(InvalidAnalyticsRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(InvalidAnalyticsRequestException ex) {
        log.warn("ANALYTICS_RULE_REJECTED - {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Error 400: un id o parametro no numerico (por ejemplo /sections/abc/...)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("ANALYTICS_INVALID_PARAMETER - {}", ex.getName());
        return buildResponse(HttpStatus.BAD_REQUEST,
                "El parametro '" + ex.getName() + "' no tiene un formato valido");
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