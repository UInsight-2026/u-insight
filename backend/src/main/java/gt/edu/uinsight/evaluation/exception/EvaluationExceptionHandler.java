// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

import gt.edu.uinsight.evaluation.controller.EvaluationController;
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
 * Manejador de errores acotado a {@link EvaluationController}, para no chocar
 * con un @RestControllerAdvice global de otra célula que todavía no existe
 * en el monorepo (mismo criterio que InterventionExceptionHandler de C4).
 */
@RestControllerAdvice(assignableTypes = EvaluationController.class)
public class EvaluationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(EvaluationExceptionHandler.class);

    @ExceptionHandler(EvaluationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEvaluationNotFound(EvaluationNotFoundException ex,
                                                                    HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "EVALUATION_NOT_FOUND", ex.getMessage(), null, request);
    }

    @ExceptionHandler(SectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSectionNotFound(SectionNotFoundException ex,
                                                                 HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "SECTION_NOT_FOUND", ex.getMessage(), null, request);
    }

    @ExceptionHandler(SectionNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleSectionNotActive(SectionNotActiveException ex,
                                                                  HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "SECTION_NOT_ACTIVE", ex.getMessage(), null, request);
    }

    @ExceptionHandler(WeightLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleWeightLimitExceeded(WeightLimitExceededException ex,
                                                                     HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "WEIGHT_LIMIT_EXCEEDED", ex.getMessage(), null, request);
    }

    @ExceptionHandler(EvaluationNotEditableException.class)
    public ResponseEntity<ErrorResponse> handleNotEditable(EvaluationNotEditableException ex,
                                                             HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "EVALUATION_NOT_EDITABLE", ex.getMessage(), null, request);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidStatusTransitionException ex,
                                                                   HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "INVALID_STATUS_TRANSITION", ex.getMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                            HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Los datos enviados no son válidos",
                details, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        String traceId = resolveTraceId(request);
        log.error("INTERNAL_ERROR traceId={} message={}", traceId, ex.getMessage(), ex);
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR", "Ocurrió un error inesperado", null, traceId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message,
                                                 List<String> details, HttpServletRequest request) {
        String traceId = resolveTraceId(request);
        log.warn("{} traceId={} message={}", error, traceId, message);
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status.value(), error, message,
                details, traceId);
        return ResponseEntity.status(status).body(body);
    }

    private String resolveTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }
}
