// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Grupo de Swagger para los endpoints de la célula A5. */
@Configuration
public class EvaluationOpenApiConfig {

    @Bean
    public GroupedOpenApi evaluationsApi() {
        return GroupedOpenApi.builder()
                .group("A5 - evaluations")
                .pathsToMatch("/api/v1/evaluations/**", "/api/v1/sections/*/evaluations")
                .build();
    }
}