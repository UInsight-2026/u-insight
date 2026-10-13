//Modifica semana 5 
package gt.edu.uinsight.report.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReportLogger {

    private static final Logger log = LoggerFactory.getLogger("gt.edu.uinsight.report");

 
    private static final String SERVICE = "report";

    
    private static final String SIN_RECURSO = "-";


    public String start(String operation, String resourceId) {
        String traceId = newTraceId();
        log.info("service={} event=OPERATION_STARTED operation={} resourceId={} traceId={} message={}",
                SERVICE, operation, valorO(resourceId), traceId, "inicio de la consulta");
        return traceId;
    }

    public void success(String traceId, String operation, String resourceId,
                        long startedAtNanos, String message) {
        long durationMs = (System.nanoTime() - startedAtNanos) / 1_000_000;
        log.info("service={} event=OPERATION_SUCCESS operation={} resourceId={} traceId={} "
                        + "status=200 durationMs={} message={}",
                SERVICE, operation, valorO(resourceId), traceId, durationMs, message);
    }

    public void rejected(String traceId, String operation, String resourceId, String reason) {
        log.warn("service={} event=BUSINESS_RULE_REJECTED operation={} resourceId={} traceId={} "
                        + "status=400 message={}",
                SERVICE, operation, valorO(resourceId), traceId, reason);
    }

    public void error(String traceId, String operation, String resourceId, String message) {
        log.error("service={} event=OPERATION_ERROR operation={} resourceId={} traceId={} "
                        + "status=500 message={}",
                SERVICE, operation, valorO(resourceId), traceId, message);
    }

    private static String valorO(String resourceId) {
        return (resourceId == null || resourceId.isBlank()) ? SIN_RECURSO : resourceId;
    }

    /** Un solo lugar genera los traceId de C5, para que el del log y el del JSON coincidan. */
    public static String newTraceId() {
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}