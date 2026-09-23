package gt.edu.uinsight.system.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Emisor unico de los eventos de log estructurado del modulo system.
 *
 * Cada evento se escribe como una sola linea JSON con los campos exigidos por la
 * seccion 10.2 del documento del proyecto: timestamp, level, service, module,
 * operation, method, path, status, durationMs, traceId y message.
 *
 * Los campos de contexto HTTP (method, path, traceId, durationMs) se resuelven
 * solos a partir de la peticion en curso, de modo que quien registra un evento
 * solo tiene que indicar la operacion, el estado y el mensaje.
 */
@Component
public class SystemEventLogger {

    /** Atributo de la peticion donde RequestTraceFilter deja el traceId. */
    public static final String TRACE_ID_ATTRIBUTE = "uinsight.traceId";

    /** Atributo de la peticion donde RequestTraceFilter deja el instante de inicio. */
    public static final String START_TIME_ATTRIBUTE = "uinsight.startTime";

    private static final Logger log = LoggerFactory.getLogger("gt.edu.uinsight.system.events");

    private static final String SERVICE = "u-insight";
    private static final String MODULE = "system";

    /**
     * Mapper propio del logger. No se inyecta el del contexto: Spring Boot 4 no
     * publica un ObjectMapper como bean salvo que se anada spring-boot-starter-json,
     * y el pom.xml es un archivo compartido por las 21 celulas.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Operacion completada con exito. */
    public void info(String operation, Integer status, String message) {
        event("INFO", operation, status, null, message, currentRequest());
    }

    /** Regla de negocio rechazada o peticion invalida: la aplicacion sigue sana. */
    public void warn(String operation, Integer status, String message) {
        event("WARN", operation, status, null, message, currentRequest());
    }

    /** Fallo no esperado. */
    public void error(String operation, Integer status, String message) {
        event("ERROR", operation, status, null, message, currentRequest());
    }

    /**
     * Variante completa. La usan el filtro de peticiones y el listener de arranque,
     * que corren fuera del alcance de RequestContextHolder o sin peticion asociada.
     */
    public void event(String level,
                      String operation,
                      Integer status,
                      Long durationMs,
                      String message,
                      HttpServletRequest request) {

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("timestamp", Instant.now().toString());
        entry.put("level", level);
        entry.put("service", SERVICE);
        entry.put("module", MODULE);
        entry.put("operation", operation);
        entry.put("method", request != null ? request.getMethod() : null);
        entry.put("path", request != null ? request.getRequestURI() : null);
        entry.put("status", status);
        entry.put("durationMs", durationMs != null ? durationMs : elapsedMillis(request));
        entry.put("traceId", traceId(request));
        entry.put("message", message);

        write(level, serialize(entry));
    }

    /** traceId de la peticion en curso, o null si el evento no nace de una peticion. */
    public String currentTraceId() {
        return traceId(currentRequest());
    }

    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }

    private String traceId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object traceId = request.getAttribute(TRACE_ID_ATTRIBUTE);
        return traceId != null ? traceId.toString() : null;
    }

    private Long elapsedMillis(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object startTime = request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime instanceof Long start) {
            return (System.nanoTime() - start) / 1_000_000;
        }
        return null;
    }

    private String serialize(Map<String, Object> entry) {
        try {
            return MAPPER.writeValueAsString(entry);
        } catch (JsonProcessingException exception) {
            // Un evento de log jamas debe tumbar la operacion que lo genero.
            return "{\"level\":\"ERROR\",\"operation\":\"LOG_SERIALIZATION_FAILED\"}";
        }
    }

    private void write(String level, String line) {
        switch (level) {
            case "ERROR" -> log.error(line);
            case "WARN" -> log.warn(line);
            default -> log.info(line);
        }
    }
}
