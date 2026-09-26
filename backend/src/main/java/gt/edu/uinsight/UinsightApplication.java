package gt.edu.uinsight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class UinsightApplication {

    private static final Logger log = LoggerFactory.getLogger(UinsightApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UinsightApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("APPLICATION_STARTED");
    }
}
