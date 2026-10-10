package gt.edu.uinsight.alert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gt.edu.uinsight.alert.engine.RiskEngine;

@Configuration
public class RiskEngineConfig {

    @Bean
    public RiskEngine riskEngine() {
        return new RiskEngine();
    }
}
