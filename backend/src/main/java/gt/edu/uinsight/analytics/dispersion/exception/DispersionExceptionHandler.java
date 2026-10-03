```java
package gt.edu.uinsight.analytics.dispersion.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DispersionExceptionHandler {

    @ExceptionHandler(DispersionDatosInvalidosException.class)
    public ResponseEntity<Map<String, Object>> handleDatosInvalidos(
            DispersionDatosInvalidosException ex) {

        return buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Datos inválidos",
                ex.getMessage());
    }

    @ExceptionHandler(DispersionDatosInsuficientesException.class)
    public ResponseEntity<Map<String, Object>> handleDatosInsuficientes(
            DispersionDatosInsuficientesException ex) {

        return buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Datos insuficientes",
                ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String error,
            String message) {

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", error,
                "message", message,
                "traceId", UUID.randomUUID().toString()
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }
}
```
