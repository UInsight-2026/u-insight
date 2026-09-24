package gt.edu.uinsight.grade.exception;

import gt.edu.uinsight.grade.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Manejo de errores acotado al modulo de calificaciones (celula A6).
 * No se declara como manejador global de la aplicacion para no interferir
 * con el manejador global que agregue la celula A1.
 */
@RestControllerAdvice(basePackages = "gt.edu.uinsight.grade")
public class GradeExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(StudentNotEnrolledException.class)
    public ResponseEntity<ApiError> handleNotEnrolled(StudentNotEnrolledException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateGradeException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateGradeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidScoreException.class)
    public ResponseEntity<ApiError> handleInvalidScore(InvalidScoreException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiError> handleInvalidRequest(InvalidRequestException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiError error = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
            message, request.getRequestURI(), null);
        return ResponseEntity.status(status).body(error);
    }
}
