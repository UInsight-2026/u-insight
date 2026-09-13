package com.uinsight.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("U-Insight API - Célula A4")
                        .version("1.0.0")
                        .description("API REST para la gestión de Secciones e Inscripciones de U-Insight."));
    }
}