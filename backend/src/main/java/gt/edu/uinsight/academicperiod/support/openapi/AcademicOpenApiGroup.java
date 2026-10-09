package gt.edu.uinsight.academicperiod.support.openapi;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Grupo de Swagger de la celula A1. Cuando alguna celula define un GroupedOpenApi,
 * Swagger UI solo lista los grupos definidos; este grupo garantiza que los 12
 * endpoints de A1 aparezcan en el selector. Solo incluye rutas de A1.
 */
@Configuration
public class AcademicOpenApiGroup {

    @Bean
    public GroupedOpenApi academicApi() {
        return GroupedOpenApi.builder()
                .group("A1 - academic-periods-courses")
                .pathsToMatch("/api/v1/academic-periods", "/api/v1/academic-periods/**",
                        "/api/v1/courses", "/api/v1/courses/{id}", "/api/v1/courses/{id}/status",
                        "/api/v1/courses/code/**")
                .build();
    }
}
