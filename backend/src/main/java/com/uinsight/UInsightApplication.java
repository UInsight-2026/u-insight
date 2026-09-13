package com.uinsight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UInsightApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UInsightApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UInsightApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("APPLICATION_STARTED: Modulo U-Insight Celula A4 iniciado correctamente.");
    }
}