// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.exception;

import gt.edu.uinsight.evaluation.controller.EvaluationController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Manejo de errores del módulo A5, acotado a {@link EvaluationController}
 * para no interceptar excepciones de otras células. Tiene la máxima prioridad
 * para que ningún @RestControllerAdvice global de otra célula (p. ej.
 * GlobalAnalyticsExceptionHandler) se quede con las excepciones de A5.
 *
 * Formato estándar U-Insight: { timestamp, status, error, message, details, traceId }.
 *
 * | Código | error                                       |
 * |--------|---------------------------------------------|
 * | 400    | VALIDATION_ERROR, MALFORMED_REQUEST         |
 * | 404    | EVALUATION_NOT_FOUND, SECTION_NOT_FOUND     |
 * | 409    | EVALUATION_NOT_EDITABLE, INVALID_STATUS_TRANSITION |
 * | 422    | SECTION_NOT_ACTIVE, WEIGHT_LIMIT_EXCEEDED   |
 * | 500    | INTERNAL_ERROR                              |
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = EvaluationController.class)
public class EvaluationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(EvaluationExceptionHandler.class);

    static final int BAD_REQUEST = 400;
    static final int NOT_FOUND = 404;
    static final int CONFLICT = 409;
    static final int UNPROCESSABLE = 422;
    static final int INTERNAL = 500;

    @ExceptionHandler(EvaluationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEvaluationNotFound(EvaluationNotFoundException ex,
                                                                  HttpServletRequest request) {
        return build(NOT_FOUND, "EVALUATION_NOT_FOUND", ex.getMessage(), null, request);
    }

    @ExceptionHandler(SectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSectionNotFound(SectionNotFoundException ex,
                                                               HttpServletRequest request) {
        return build(NOT_FOUND, "SECTION_NOT_FOUND", ex.getMessage(), null, request);
    }

    @ExceptionHandler(SectionNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleSectionNotActive(SectionNotActiveException ex,
                                                                HttpServletRequest request) {
        return build(UNPROCESSABLE, "SECTION_NOT_ACTIVE", ex.getMessage(), null, request);
    }

    @ExceptionHandler(WeightLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleWeightLimitExceeded(WeightLimitExceededException ex,
                                                                   HttpServletRequest request) {
        return build(UNPROCESSABLE, "WEIGHT_LIMIT_EXCEEDED", ex.getMessage(), null, request);
    }

    @ExceptionHandler(EvaluationNotEditableException.class)
    public ResponseEntity<ErrorResponse> handleNotEditable(EvaluationNotEditableException ex,
                                                           HttpServletRequest request) {
        return build(CONFLICT, "EVALUATION_NOT_EDITABLE", ex.getMessage(), null, request);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidStatusTransitionException ex,
                                                                 HttpServletRequest request) {
        return build(CONFLICT, "INVALID_STATUS_TRANSITION", ex.getMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .sorted()
                .toList();
        return build(BAD_REQUEST, "VALIDATION_ERROR", "Los datos enviados no son válidos", details, request);
    }

    /** JSON mal formado, fecha con formato inválido o número no numérico. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        return build(BAD_REQUEST, "MALFORMED_REQUEST",
                "El cuerpo de la petición no es un JSON válido o tiene tipos/fechas incorrectos (fecha: yyyy-MM-dd)",
                null, request);
    }

    /** Ej. GET /api/v1/evaluations/abc */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        return build(BAD_REQUEST, "VALIDATION_ERROR",
                "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue(),
                null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        String traceId = resolveTraceId(request);
        log.error("INTERNAL_ERROR method={} path={} traceId={} message={}",
                request.getMethod(), request.getRequestURI(), traceId, ex.getMessage(), ex);
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), INTERNAL, "INTERNAL_ERROR",
                "Ocurrió un error inesperado", null, traceId);
        return ResponseEntity.status(INTERNAL).body(body);
    }

    private ResponseEntity<ErrorResponse> build(int status, String error, String message,
                                                List<String> details, HttpServletRequest request) {
        String traceId = resolveTraceId(request);
        log.warn("{} status={} method={} path={} traceId={} message={}",
                error, status, request.getMethod(), request.getRequestURI(), traceId, message);
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status, error, message,
                details == null ? List.of() : details, traceId);
        return ResponseEntity.status(status).body(body);
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
