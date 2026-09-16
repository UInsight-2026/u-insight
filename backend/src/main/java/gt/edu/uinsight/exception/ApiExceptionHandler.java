package gt.edu.uinsight.exception;

import gt.edu.uinsight.common.ErrorResponse;
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

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        String traceId = newTraceId();
        log.warn("RESOURCE_NOT_FOUND traceId={} message={}", traceId, ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), List.of(), traceId);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        String traceId = newTraceId();
        log.warn("DUPLICATE_RESOURCE traceId={} message={}", traceId, ex.getMessage());
        return build(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", ex.getMessage(), List.of(), traceId);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
        String traceId = newTraceId();
        log.warn("VALIDATION_ERROR traceId={} message={}", traceId, ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BUSINESS_RULE_VIOLATION", ex.getMessage(), List.of(), traceId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String traceId = newTraceId();
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .toList();
        log.warn("VALIDATION_ERROR traceId={} details={}", traceId, details);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Uno o mas campos no son validos", details, traceId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        String traceId = newTraceId();
        log.error("UNEXPECTED_ERROR traceId={}", traceId, ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Ocurrio un error inesperado", List.of(), traceId);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message,
                                                 List<String> details, String traceId) {
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status.value(), error, message, details, traceId);
        return ResponseEntity.status(status).body(body);
    }

    private String newTraceId() {
        return UUID.randomUUID().toString();
    }
}
