package gt.edu.uinsight.academicperiod.support.logging;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra el interceptor de A1 solo sobre las rutas de periodos academicos y cursos.
 * No afecta a los endpoints de otras celulas.
 */
@Configuration
public class AcademicWebConfig implements WebMvcConfigurer {

    private final AcademicRequestInterceptor interceptor;

    public AcademicWebConfig(AcademicRequestInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Rutas exactas de A1: /api/v1/courses/** no se usa para no capturar rutas
        // que otras celulas pudieran colgar bajo /courses en el futuro.
        registry.addInterceptor(interceptor)
                .addPathPatterns(
                        "/api/v1/academic-periods",
                        "/api/v1/academic-periods/active",
                        "/api/v1/academic-periods/{id}",
                        "/api/v1/academic-periods/{id}/status",
                        "/api/v1/courses",
                        "/api/v1/courses/code/{code}",
                        "/api/v1/courses/{id}",
                        "/api/v1/courses/{id}/status");
    }
}
