package gt.edu.uinsight.academicperiod.support.exception;

import gt.edu.uinsight.academicperiod.controller.AcademicPeriodController;
import gt.edu.uinsight.academicperiod.support.logging.AcademicEventLogger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Manejador de errores de la celula A1. Esta acotado a los controladores de A1 y
 * tiene la mayor precedencia, de modo que no captura excepciones de otras celulas
 * y los manejadores globales no capturan las de A1.
 *
 * <p>Mapeo: validacion y RN-02/RN-08 -> 400 VALIDATION_ERROR; recurso inexistente
 * -> 404 NOT_FOUND; RN-01/RN-03/RN-04/RN-06/RN-07 -> 409 CONFLICT; resto -> 500
 * INTERNAL_ERROR.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = {AcademicPeriodController.class})
public class AcademicExceptionHandler {

    private final AcademicEventLogger eventLogger;

    public AcademicExceptionHandler(AcademicEventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    @ExceptionHandler(AcademicResourceNotFoundException.class)
    public ResponseEntity<AcademicErrorResponse> handleNotFound(AcademicResourceNotFoundException ex) {
        eventLogger.warn("RESOURCE_NOT_FOUND", 404, ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), List.of());
    }

    @ExceptionHandler(AcademicBusinessRuleException.class)
    public ResponseEntity<AcademicErrorResponse> handleBusinessRule(AcademicBusinessRuleException ex) {
        HttpStatus status = ex.getStatus();
        String error = status == HttpStatus.CONFLICT ? "CONFLICT" : "VALIDATION_ERROR";
        if (ex.getRuleId() == null) {
            eventLogger.warn("VALIDATION_ERROR", status.value(), ex.getMessage());
            return build(status, error, ex.getMessage(), List.of());
        }
        String operation = ex.isDuplicate() ? "DUPLICATE_RESOURCE" : "BUSINESS_RULE_REJECTED";
        eventLogger.warn(operation, status.value(), "[" + ex.getRuleId() + "] " + ex.getMessage());
        return build(status, error, ex.getMessage(), List.of("rule: " + ex.getRuleId()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AcademicErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .toList();
        eventLogger.warn("VALIDATION_ERROR", 400, "Validation failed: " + details);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", details);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            PropertyReferenceException.class})
    public ResponseEntity<AcademicErrorResponse> handleBadRequest(Exception ex) {
        String message = ex instanceof HttpMessageNotReadableException
                ? "El cuerpo de la peticion no es valido: revise el formato JSON y las fechas (yyyy-MM-dd)"
                : ex.getMessage();
        eventLogger.warn("VALIDATION_ERROR", 400, message);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AcademicErrorResponse> handleIntegrity(DataIntegrityViolationException ex) {
        eventLogger.warn("DUPLICATE_RESOURCE", 409, "Data integrity violation");
        return build(HttpStatus.CONFLICT, "CONFLICT",
                "La operacion viola una restriccion de unicidad de la base de datos", List.of());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<AcademicErrorResponse> handleDatabase(DataAccessException ex) {
        eventLogger.error("DATABASE_ERROR", 500, ex.getClass().getSimpleName());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Error de acceso a datos", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AcademicErrorResponse> handleUnexpected(Exception ex) {
        eventLogger.error("UNEXPECTED_ERROR", 500, ex.getClass().getSimpleName() + ": " + ex.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Error interno inesperado", List.of());
    }

    private ResponseEntity<AcademicErrorResponse> build(HttpStatus status, String error, String message,
                                                        List<String> details) {
        AcademicErrorResponse body = new AcademicErrorResponse(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                status.value(),
                error,
                message,
                details,
                eventLogger.currentTraceId()
        );
        return ResponseEntity.status(status).body(body);
    }
}
