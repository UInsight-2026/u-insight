package gt.edu.uinsight.system.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestTraceFilter extends OncePerRequestFilter {

    private static final String MODULE_PATH = "/api/v1/system";
    private static final String MDC_KEY = "traceId";

    private final SystemEventLogger eventLogger;

    public RequestTraceFilter(SystemEventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(MODULE_PATH);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String traceId = "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long startTime = System.nanoTime();

        request.setAttribute(SystemEventLogger.TRACE_ID_ATTRIBUTE, traceId);
        request.setAttribute(SystemEventLogger.START_TIME_ATTRIBUTE, startTime);
        MDC.put(MDC_KEY, traceId);

        eventLogger.event("INFO", "OPERATION_STARTED", null, 0L,
                "Request received", request);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            int status = response.getStatus();

            eventLogger.event(levelFor(status), "OPERATION_COMPLETED", status, durationMs,
                    "Request completed", request);

            MDC.remove(MDC_KEY);
        }
    }

    private String levelFor(int status) {
        if (status >= 500) {
            return "ERROR";
        }
        if (status >= 400) {
            return "WARN";
        }
        return "INFO";
    }
}
