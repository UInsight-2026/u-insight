package gt.edu.uinsight.alert.config;

import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alertrule.service.AlertRuleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RiskEngineConfig {

    @Bean
    public RiskEngine riskEngine(AlertRuleService alertRuleService) {
        return new RiskEngine(alertRuleService);
    }
}
