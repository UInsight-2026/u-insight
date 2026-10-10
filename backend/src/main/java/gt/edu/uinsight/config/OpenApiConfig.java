package gt.edu.uinsight.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Informacion general de la documentacion OpenAPI del proyecto. Es comun a todas
 * las celulas: cada una documenta sus endpoints con @Tag y @Operation en sus
 * propios controladores.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI uinsightOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("U-Insight API")
                        .version("v1")
                        .description("Plataforma de Analítica Académica y Alertas Tempranas"));
    }
}
