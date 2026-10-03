package gt.edu.uinsight.analytics.dispersion.exception;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
 
import java.time.LocalDateTime;
import java.util.Map;
 
@RestControllerAdvice
public class DispersionExceptionHandler {
 
    @ExceptionHandler(SeccionNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> 
manejarSeccionNoEncontrada(SeccionNoEncontradaException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }
 
    @ExceptionHandler(CursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> 
manejarCursoNoEncontrado(CursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }
 
    @ExceptionHandler(DispersionDatosInsuficientesException.class)
    public ResponseEntity<Map<String, Object>> 
manejarDatosInsuficientes(DispersionDatosInsuficientesException ex) {
        return construirRespuesta(HttpStatus.UNPROCESSABLE_ENTITY, 
ex.getMessage());
    }
 
    @ExceptionHandler(DispersionDatosInvalidosException.class)
    public ResponseEntity<Map<String, Object>> 
manejarDatosInvalidos(DispersionDatosInvalidosException ex) {
        return construirRespuesta(HttpStatus.UNPROCESSABLE_ENTITY, 
ex.getMessage());
    }
 
    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus 
status, String mensaje) {
        Map<String, Object> cuerpo = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", mensaje
        );
        return ResponseEntity.status(status).body(cuerpo);
    }
}
