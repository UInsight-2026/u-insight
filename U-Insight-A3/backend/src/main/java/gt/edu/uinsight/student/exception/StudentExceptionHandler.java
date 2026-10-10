package gt.edu.uinsight.student.exception;

import gt.edu.uinsight.student.controller.StudentController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice(assignableTypes = StudentController.class)
public class StudentExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(StudentExceptionHandler.class);

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<StudentApiErrorResponse> handleNotFound(
            StudentNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "STUDENT_NOT_FOUND", ex.getMessage(), null, request);
    }

    @ExceptionHandler(DuplicateStudentCodeException.class)
    public ResponseEntity<StudentApiErrorResponse> handleDuplicate(
            DuplicateStudentCodeException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "DUPLICATE_STUDENT_CODE", ex.getMessage(), null, request);
    }

    @ExceptionHandler(InactiveStudentException.class)
    public ResponseEntity<StudentApiErrorResponse> handleInactive(
            InactiveStudentException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "STUDENT_INACTIVE", ex.getMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StudentApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Los datos enviados no son válidos", details, request);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class})
    public ResponseEntity<StudentApiErrorResponse> handleMalformedRequest(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST",
                "La solicitud contiene datos inválidos", List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StudentApiErrorResponse> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        log.error("ERRORS path={} message={}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Ocurrió un error inesperado", null, request);
    }

    private ResponseEntity<StudentApiErrorResponse> build(
            HttpStatus status,
            String error,
            String message,
            List<String> details,
            HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        StudentApiErrorResponse body = new StudentApiErrorResponse(
                LocalDateTime.now(), status.value(), error, message, details, traceId);
        return ResponseEntity.status(status).body(body);
    }
}