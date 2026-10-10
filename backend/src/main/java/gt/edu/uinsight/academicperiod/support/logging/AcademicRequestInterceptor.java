package gt.edu.uinsight.academicperiod.support.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Marca el inicio de cada peticion a los endpoints de A1: asigna el traceId, guarda
 * la hora de inicio para calcular durationMs y registra OPERATION_STARTED y, si la
 * peticion termina bien, OPERATION_COMPLETED. Los errores los registra el
 * manejador de excepciones de A1.
 *
 * <p>Solo se registra para las rutas de A1 (ver {@link AcademicWebConfig}).
 */
@Component
public class AcademicRequestInterceptor implements HandlerInterceptor {

    private final AcademicEventLogger eventLogger;

    public AcademicRequestInterceptor(AcademicEventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(AcademicEventLogger.START_TIME_ATTRIBUTE, System.nanoTime());
        response.setHeader("X-Trace-Id", eventLogger.traceId(request));
        eventLogger.info("OPERATION_STARTED", null, "Operation started");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (response.getStatus() < 400) {
            eventLogger.info("OPERATION_COMPLETED", response.getStatus(), "Operation completed");
        }
    }
}
