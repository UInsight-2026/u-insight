package gt.edu.uinsight.academicperiod.support.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Logging estructurado de la celula A1 (seccion 10.2): una linea JSON por evento con
 * timestamp, level, service, module, operation, method, path, status, durationMs,
 * traceId y message.
 *
 * <p>No registra credenciales ni datos personales: solo ids, codigos y nombres de
 * periodos y cursos.
 */
@Component
public class AcademicEventLogger {

    public static final String TRACE_ID_ATTRIBUTE = "a1.traceId";
    public static final String START_TIME_ATTRIBUTE = "a1.startTime";

    /** Atributo y clave MDC que usa el filtro de trazas de C7, si ya corrio. */
    private static final String SHARED_TRACE_ID_ATTRIBUTE = "uinsight.traceId";
    private static final String SHARED_TRACE_ID_MDC = "traceId";

    private static final Logger log = LoggerFactory.getLogger("gt.edu.uinsight.academic");
    private static final ObjectMapper JSON = new ObjectMapper();

    private static final String SERVICE = "u-insight";
    private static final String MODULE = "academic";

    public void info(String operation, Integer status, String message) {
        event("INFO", operation, status, message);
    }

    public void warn(String operation, Integer status, String message) {
        event("WARN", operation, status, message);
    }

    public void error(String operation, Integer status, String message) {
        event("ERROR", operation, status, message);
    }

    /**
     * Devuelve el traceId de la peticion en curso. Reutiliza el de C7 si esta
     * disponible; si no, genera uno propio y lo guarda en la peticion.
     */
    public String traceId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object own = request.getAttribute(TRACE_ID_ATTRIBUTE);
        if (own != null) {
            return own.toString();
        }
        Object shared = request.getAttribute(SHARED_TRACE_ID_ATTRIBUTE);
        String traceId = shared != null ? shared.toString() : MDC.get(SHARED_TRACE_ID_MDC);
        if (traceId == null) {
            traceId = "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        request.setAttribute(TRACE_ID_ATTRIBUTE, traceId);
        return traceId;
    }

    public String currentTraceId() {
        return traceId(currentRequest());
    }

    private void event(String level, String operation, Integer status, String message) {
        HttpServletRequest request = currentRequest();

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("timestamp", Instant.now().toString());
        entry.put("level", level);
        entry.put("service", SERVICE);
        entry.put("module", MODULE);
        entry.put("operation", operation);
        entry.put("method", request != null ? request.getMethod() : null);
        entry.put("path", request != null ? request.getRequestURI() : null);
        entry.put("status", status);
        entry.put("durationMs", elapsedMillis(request));
        entry.put("traceId", traceId(request));
        entry.put("message", message);

        String line = toJson(entry);
        switch (level) {
            case "ERROR" -> log.error(line);
            case "WARN" -> log.warn(line);
            default -> log.info(line);
        }
    }

    private Long elapsedMillis(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object start = request.getAttribute(START_TIME_ATTRIBUTE);
        return start instanceof Long startNanos ? (System.nanoTime() - startNanos) / 1_000_000 : null;
    }

    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }

    private String toJson(Map<String, Object> entry) {
        try {
            return JSON.writeValueAsString(entry);
        } catch (JsonProcessingException ex) {
            return entry.toString();
        }
    }
}
