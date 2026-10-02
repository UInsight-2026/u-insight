package gt.edu.uinsight.imports.exception;

import gt.edu.uinsight.imports.util.ImportLogEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

@RestControllerAdvice(basePackages = "gt.edu.uinsight.imports")
public class ImportExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ImportExceptionHandler.class);

    @ExceptionHandler(ImportNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ImportNotFoundException ex,
                                                          WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("{} traceId={} message={}", ImportLogEvents.RESOURCE_NOT_FOUND, traceId, ex.getMessage());
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false),
                traceId
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(InvalidCsvFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCsv(InvalidCsvFileException ex,
                                                            WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("{} traceId={} message={}", ImportLogEvents.INVALID_CSV_FILE, traceId, ex.getMessage());
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false),
                traceId
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ImportNotConfirmableException.class)
    public ResponseEntity<ErrorResponse> handleNotConfirmable(ImportNotConfirmableException ex,
                                                                WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("{} traceId={} message={}", ImportLogEvents.BUSINESS_RULE_REJECTED, traceId, ex.getMessage());
        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getDescription(false),
                traceId
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("{} traceId={} message={}", ImportLogEvents.INTERNAL_ERROR, traceId, ex.getMessage(), ex);
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocurrió un error inesperado al procesar la solicitud.",
                request.getDescription(false),
                traceId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
