package gt.edu.uinsight.analytics.centraltendency.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Acotado a los controllers de B1: un manejador global con catch-all
 * convertiria en 500 los errores de los demas modulos y las rutas inexistentes
 * (confirmado en el informe de pruebas de integracion de A5, 2-oct-2026:
 * capturaba tambien errores de A2, A4 y A6).
 *
 * Responde con el formato estándar U-Insight (mismo contrato que A5, B4 y C4):
 * {@code { timestamp, status, error, message, details, traceId }}.
 */
@RestControllerAdvice(basePackages = "gt.edu.uinsight.analytics.centraltendency")
public class GlobalAnalyticsExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalAnalyticsExceptionHandler.class);

    // Error 400: Parámetro inválido o regla rechazada
    @ExceptionHandler(InvalidAnalyticsRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(InvalidAnalyticsRequestException ex, HttpServletRequest request) {
        // Evento obligatorio: business_rule_rejected
        log.warn("service=B1 event=business_rule_rejected operation=validate_request message=\"{}\"", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), request);
    }

    // Error 400: un id o parametro no numerico (por ejemplo /sections/abc/...)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        // Evento obligatorio: business_rule_rejected
        log.warn("service=B1 event=business_rule_rejected operation=validate_parameter parameter={} message=\"{}\"", ex.getName(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "El parametro '" + ex.getName() + "' no tiene un formato valido", request);
    }

    // Error 404: Sección o curso inexistente
    @ExceptionHandler(AnalyticsResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(AnalyticsResourceNotFoundException ex, HttpServletRequest request) {
        // Agregamos log de rechazo que antes faltaba
        log.warn("service=B1 event=business_rule_rejected operation=fetch_resource message=\"{}\"", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request);
    }

    // Error 502: Error de integración al consultar dependencias
    @ExceptionHandler(GradeDataIntegrationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrationError(GradeDataIntegrationException ex, HttpServletRequest request) {
        // Evento obligatorio: integration_failed. Usamos dependency=A6 basado en el ejemplo del ingeniero.
        log.error("service=B1 event=integration_failed operation=fetch_grades dependency=A6 message=\"{}\"", ex.getMessage());
        return buildResponse(HttpStatus.BAD_GATEWAY, "INTEGRATION_ERROR", ex.getMessage(), request);
    }

    // Error 500: Error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception ex, HttpServletRequest request) {
        // Evento obligatorio: operation_failed. Se pasa 'ex' al final para imprimir el stacktrace.
        log.error("service=B1 event=operation_failed operation=central_tendency_calculation message=\"{}\"", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "Ocurrio un error inesperado al procesar la solicitud", request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message,
                                                          HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(), status.value(), error, message, List.of(), resolveTraceId(request));
        return new ResponseEntity<>(response, status);
    }

    private String resolveTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeader("X-Correlation-ID");
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }
}
