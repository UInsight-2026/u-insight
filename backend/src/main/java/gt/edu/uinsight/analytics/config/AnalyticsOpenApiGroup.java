package gt.edu.uinsight.analytics.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsOpenApiGroup {

    @Bean
    public GroupedOpenApi analyticsApi() {
        return GroupedOpenApi.builder()
                .group("B - Analítica y riesgo")
                .pathsToMatch(
                        "/api/v1/analytics/**",
                        "/api/v1/alert-rules",
                        "/api/v1/alert-rules/**",
                        "/api/v1/integration/b7/**",
                        "/api/v1/risk-evaluation/**")
                .build();
    }
}
