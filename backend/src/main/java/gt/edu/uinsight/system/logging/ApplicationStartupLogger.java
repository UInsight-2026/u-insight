package gt.edu.uinsight.system.logging;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ApplicationStartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    private final SystemEventLogger eventLogger;

    public ApplicationStartupLogger(SystemEventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Duration timeTaken = event.getTimeTaken();

        eventLogger.event(
                "INFO",
                "APPLICATION_STARTED",
                null,
                timeTaken != null ? timeTaken.toMillis() : null,
                "U-Insight application is ready to accept requests",
                null
        );
    }
}
