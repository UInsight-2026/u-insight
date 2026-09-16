package gt.edu.uinsight.system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja el Error 400 - Petición incorrecta (ej. enviar texto en el ID en lugar de un número)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleBadRequest(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error 400: Petición inválida. Verifica los datos enviados.");
    }

    // Maneja el Error 404 - Ruta no encontrada
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error 404: El recurso que buscas no existe.");
    }

    // Maneja el Error 500 - Cualquier error interno que apague o falle en el servidor
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error 500: Error interno del servidor.");
    }
}