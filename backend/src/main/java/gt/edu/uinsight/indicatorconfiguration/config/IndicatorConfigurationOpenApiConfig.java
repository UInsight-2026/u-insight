package gt.edu.uinsight.indicatorconfiguration.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndicatorConfigurationOpenApiConfig {

    @Bean
    public GroupedOpenApi indicatorConfigurationApi() {
        return GroupedOpenApi.builder()
                .group("C1 - indicator-configurations")
                .pathsToMatch("/api/v1/indicator-configurations/**")
                .build();
    }
}
