//SEMANA 3
package gt.edu.uinsight.report.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReportLogger {

    private static final Logger log = LoggerFactory.getLogger("gt.edu.uinsight.report");
    private static final String MODULE = "report";

    /** Registra el inicio y devuelve el traceId que acompaña al resto de la operación. */
    public String start(String operation, String path) {
        String traceId = newTraceId();
        log.info("REPORT_OPERATION_STARTED module={} operation={} method=GET path={} traceId={}",
                MODULE, operation, path, traceId);
        return traceId;
    }

    public void success(String traceId, String operation, long startedAtNanos, String message) {
        long durationMs = (System.nanoTime() - startedAtNanos) / 1_000_000;
        log.info("REPORT_OPERATION_SUCCESS module={} operation={} status=200 durationMs={} traceId={} message={}",
                MODULE, operation, durationMs, traceId, message);
    }

    public void rejected(String traceId, String operation, String reason) {
        log.warn("REPORT_BUSINESS_RULE_REJECTED module={} operation={} status=400 traceId={} reason={}",
                MODULE, operation, traceId, reason);
    }

    public void error(String traceId, String operation, String message) {
        log.error("REPORT_OPERATION_ERROR module={} operation={} status=500 traceId={} message={}",
                MODULE, operation, traceId, message);
    }

    /** Un solo lugar genera los traceId de C5, para que el del log y el del JSON de error coincidan. */
    public static String newTraceId() {
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}