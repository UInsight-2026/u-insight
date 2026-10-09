package gt.edu.uinsight.report.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Grupo de Swagger de la celula C5.
 *
 * springdoc deja de mostrar la vista general en cuanto alguna celula declara un
 * GroupedOpenApi, y pasa a listar solo los grupos declarados. A1 y C1 ya declaran
 * el suyo, por lo que los cuatro endpoints de reportes dejaron de aparecer en el
 * selector aunque sus anotaciones estaban completas. Este grupo los devuelve.
 *
 * Solo incluye rutas de C5: no altera lo que publican las demas celulas.
 */
@Configuration
public class ReportOpenApiGroup {

    @Bean
    public GroupedOpenApi reportApi() {
        return GroupedOpenApi.builder()
                .group("C5 - reports")
                .pathsToMatch("/api/v1/reports/**")
                .build();
    }
}
