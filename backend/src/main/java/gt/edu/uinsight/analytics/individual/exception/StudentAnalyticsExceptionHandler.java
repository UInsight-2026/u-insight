package gt.edu.uinsight.analytics.individual.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

// Renombrado de GlobalExceptionHandler a StudentAnalyticsExceptionHandler: colisionaba
// por nombre de clase/bean con gt.edu.uinsight.common.exception.GlobalExceptionHandler
// (célula C2), y @ControllerAdvice no admite un nombre de bean explícito como @Component.
// Ambos manejadores siguen activos: este solo atiende StudentNotFoundException.
@RestControllerAdvice
public class StudentAnalyticsExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStudentNotFound(StudentNotFoundException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "STUDENT_NOT_FOUND",
                "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}