package gt.edu.uinsight.system.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprueba que cada evento sale como una sola linea JSON con los campos que
 * exige la seccion 10.2 del documento del proyecto.
 */
class SystemEventLoggerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Logger eventsLogger;
    private ListAppender<ILoggingEvent> appender;
    private SystemEventLogger eventLogger;

    @BeforeEach
    void setUp() {
        eventsLogger = (Logger) LoggerFactory.getLogger("gt.edu.uinsight.system.events");
        appender = new ListAppender<>();
        appender.start();
        eventsLogger.addAppender(appender);

        eventLogger = new SystemEventLogger();
    }

    @AfterEach
    void tearDown() {
        eventsLogger.detachAppender(appender);
    }

    @Test
    void registraTodosLosCamposDeLaSeccion10_2() throws Exception {
        eventLogger.event("INFO", "APPLICATION_STARTED", null, 3400L,
                "U-Insight application is ready to accept requests", null);

        assertEquals(1, appender.list.size());

        ILoggingEvent logged = appender.list.get(0);
        assertEquals(Level.INFO, logged.getLevel());

        JsonNode event = MAPPER.readTree(logged.getFormattedMessage());

        for (String field : new String[] {
                "timestamp", "level", "service", "module", "operation",
                "method", "path", "status", "durationMs", "traceId", "message" }) {
            assertTrue(event.has(field), "falta el campo " + field);
        }

        assertEquals("u-insight", event.get("service").asText());
        assertEquals("system", event.get("module").asText());
        assertEquals("APPLICATION_STARTED", event.get("operation").asText());
        assertEquals(3400L, event.get("durationMs").asLong());
    }

    @Test
    void unaReglaDeNegocioRechazadaSeRegistraComoWarn() throws Exception {
        eventLogger.warn("BUSINESS_RULE_REJECTED", 409,
                "A check in DOWN cannot move directly to UP");

        assertEquals(1, appender.list.size());

        ILoggingEvent logged = appender.list.get(0);
        assertEquals(Level.WARN, logged.getLevel());

        JsonNode event = MAPPER.readTree(logged.getFormattedMessage());
        assertEquals("WARN", event.get("level").asText());
        assertEquals("BUSINESS_RULE_REJECTED", event.get("operation").asText());
        assertEquals(409, event.get("status").asInt());
    }
}
