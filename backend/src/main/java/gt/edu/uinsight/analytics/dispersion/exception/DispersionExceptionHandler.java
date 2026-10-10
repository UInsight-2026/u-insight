
package gt.edu.uinsight.analytics.dispersion.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DispersionExceptionHandler {

    @ExceptionHandler(SeccionNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleSeccionNoEncontrada(
            SeccionNoEncontradaException exception) {

        return crearRespuesta(
                HttpStatus.NOT_FOUND,
                exception.getMessage());
    }

    @ExceptionHandler(CursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleCursoNoEncontrado(
            CursoNoEncontradoException exception) {

        return crearRespuesta(
                HttpStatus.NOT_FOUND,
                exception.getMessage());
    }

    @ExceptionHandler(DispersionDatosInsuficientesException.class)
    public ResponseEntity<Map<String, Object>> handleDatosInsuficientes(
            DispersionDatosInsuficientesException exception) {

        return crearRespuesta(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage());
    }

    @ExceptionHandler(DispersionDatosInvalidosException.class)
    public ResponseEntity<Map<String, Object>> handleDatosInvalidos(
            DispersionDatosInvalidosException exception) {

        return crearRespuesta(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage());
    }

    private ResponseEntity<Map<String, Object>> crearRespuesta(
            HttpStatus status,
            String mensaje) {

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put(
                "message",
                mensaje != null
                        ? mensaje
                        : "Error en el módulo de dispersión.");

        return ResponseEntity.status(status).body(body);
    }
}
