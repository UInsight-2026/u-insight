package gt.edu.uinsight.report.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Permite que el frontend de la célula C6 consuma los reportes desde el navegador.
 *
 * Se limita a /api/v1/reports/** a propósito: una configuración global cambiaría
 * el comportamiento de los endpoints de las demás células sin su consentimiento.
 */
@Configuration
public class ReportCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) 
    {
        registry.addMapping("/api/v1/reports/**")
                .allowedOrigins("*")
                .allowedMethods("GET")
                .allowedHeaders("*");
    }
}