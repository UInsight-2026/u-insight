package gt.edu.uinsight.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Habilita CORS para todo /api/v1/** durante desarrollo local, para que el
 * frontend (servido por separado, ej. http://localhost:5500) pueda llamar
 * al backend sin que el navegador bloquee la petición.
 *
 * Temporal / solo para pruebas locales: no está pensado para producción tal
 * cual (origin "*" es demasiado permisivo). Pendiente de que el equipo
 * decida la configuración real de CORS para el despliegue.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
