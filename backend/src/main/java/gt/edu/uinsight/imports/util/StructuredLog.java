package gt.edu.uinsight.imports.util;

import org.slf4j.Logger;

import java.time.Instant;

/**
 * Logging estructurado de la célula A7. Emite una línea JSON con:
 * timestamp, level, service, event, operation, resourceId y message (opcional).
 */
public final class StructuredLog {

    public static final String SERVICE = "imports";

    private StructuredLog() {
    }

    public static void info(Logger log, String event, String operation, Object resourceId, String message) {
        if (log.isInfoEnabled()) {
            log.info("{}", toJson("INFO", event, operation, resourceId, message));
        }
    }

    public static void warn(Logger log, String event, String operation, Object resourceId, String message) {
        if (log.isWarnEnabled()) {
            log.warn("{}", toJson("WARN", event, operation, resourceId, message));
        }
    }

    public static void error(Logger log, String event, String operation, Object resourceId, String message) {
        if (log.isErrorEnabled()) {
            log.error("{}", toJson("ERROR", event, operation, resourceId, message));
        }
    }

    public static String toJson(String level, String event, String operation,
                                Object resourceId, String message) {
        StringBuilder sb = new StringBuilder("{");
        campo(sb, "timestamp", Instant.now().toString(), true);
        campo(sb, "level", level, false);
        campo(sb, "service", SERVICE, false);
        campo(sb, "event", event, false);
        campo(sb, "operation", operation, false);
        if (resourceId != null) {
            campo(sb, "resourceId", String.valueOf(resourceId), false);
        }
        if (message != null && !message.isBlank()) {
            campo(sb, "message", message, false);
        }
        return sb.append('}').toString();
    }

    private static void campo(StringBuilder sb, String nombre, String valor, boolean primero) {
        if (!primero) {
            sb.append(',');
        }
        sb.append('"').append(nombre).append("\":\"").append(escapar(valor)).append('"');
    }

    private static String escapar(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}