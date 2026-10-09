package gt.edu.uinsight.system.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * Contrato OpenAPI del proyecto y documentacion de los cuatro tags de la celula C7.
 *
 * <p>Es el requisito 3 de la semana -contratos al dia- hecho visible en Swagger UI. Antes
 * de esta clase el proyecto no declaraba ningun bean {@link OpenAPI}: Swagger UI se servia
 * con el titulo generico que springdoc genera a partir del nombre de la aplicacion, sin
 * descripcion, sin version de API y con los tags ordenados alfabeticamente y sin texto mas
 * alla de la anotacion de cada controlador.
 *
 * <p>El bean solo aporta {@code Info}, {@code tags} y {@code externalDocs}. No declara
 * {@code paths} ni {@code components} a proposito: los endpoints de las demas celulas los
 * sigue descubriendo springdoc por reflexion, y nada de lo que esta clase hace los altera.
 *
 * <p>Los nombres de los tags coinciden exactamente con los de las anotaciones
 * {@code @Tag} de los controladores del modulo. Si no coincidieran, springdoc mostraria el
 * tag dos veces: el declarado aqui, vacio, y el del controlador.
 */
@Configuration
public class SystemOpenApiConfig {

    public static final String TAG_HEALTH = "System - Health";
    public static final String TAG_READINESS = "System - Readiness";
    public static final String TAG_INTEGRATION = "System - Integration";
    public static final String TAG_CHECKS = "System - Checks";

    /** Version del contrato, la misma que el prefijo de todas las rutas. */
    private static final String API_VERSION = "v1";

    private static final String REPOSITORIO = "https://github.com/UInsight-2026/u-insight";

    /**
     * {@code @Primary} porque la celula A1 declara otro bean {@link OpenAPI} en
     * {@code gt.edu.uinsight.config.OpenApiConfig} (PR #133). Con dos candidatos del mismo
     * tipo springdoc no sabe cual usar. Se marca este porque es el mas completo de los dos:
     * aporta el mismo {@code Info} mas los tags del modulo y el {@code externalDocs}.
     */
    @Bean
    @Primary
    public OpenAPI uinsightOpenAPI() {

        return new OpenAPI()
                .info(info())
                .externalDocs(new ExternalDocumentation()
                        .description("Repositorio oficial del proyecto")
                        .url(REPOSITORIO))
                .tags(tagsDelModuloC7());
    }

    /**
     * Grupo de Swagger del modulo. Las celulas A1, A5 y C1 declaran beans
     * {@link GroupedOpenApi}, y en cuanto existe al menos un grupo springdoc deja de servir
     * el contrato completo y el selector de Swagger UI solo lista los grupos declarados.
     * Sin este bean los cuatro endpoints de C7 desaparecen de la interfaz.
     */
    @Bean
    public GroupedOpenApi systemApi() {

        return GroupedOpenApi.builder()
                .group("C7 - system")
                .pathsToMatch("/api/v1/system/**")
                .build();
    }

    private Info info() {

        return new Info()
                .title("U-Insight API")
                .version(API_VERSION)
                .description("""
                        Plataforma de Analitica Academica y Alertas Tempranas.

                        Proyecto Integrador U-Insight, curso de Programacion II, ciclo \
                        academico 2026. La API esta construida por 21 celulas sobre una \
                        misma aplicacion: las secciones A y B aportan la gestion academica \
                        y la analitica, y la seccion C la gestion, la integracion y la \
                        presentacion.

                        Los cuatro endpoints agrupados bajo los tags `System - *` \
                        pertenecen a la celula C7 (calidad, integracion y APIs tecnicas) y \
                        viven en el paquete `gt.edu.uinsight.system`. No exponen datos \
                        academicos: sirven para comprobar el estado del despliegue y la \
                        integracion entre modulos.""")
                .contact(new Contact()
                        .name("Celula C7 - Calidad, integracion y APIs tecnicas"));
    }

    /**
     * Los cuatro tags del modulo, en orden de uso y no alfabetico: primero el diagnostico
     * mas barato, al final el registro historico.
     */
    private List<Tag> tagsDelModuloC7() {

        return List.of(
                new Tag()
                        .name(TAG_HEALTH)
                        .description("""
                                Comprobacion de estado del servicio. \
                                `GET /api/v1/system/health` responde 200 siempre que la \
                                aplicacion este en pie, con el estado de la base de datos. \
                                Es el chequeo mas barato: no consulta a otros modulos."""),
                new Tag()
                        .name(TAG_READINESS)
                        .description("""
                                Disponibilidad para recibir trafico. \
                                `GET /api/v1/system/readiness` responde 200 si la base de \
                                datos, las configuraciones criticas y los servicios de los \
                                que depende el modulo estan disponibles, y 503 si alguno \
                                no lo esta, nombrando en el cuerpo el componente que \
                                fallo. Lo consumen otras celulas y la asignatura de \
                                Sistemas Operativos I."""),
                new Tag()
                        .name(TAG_INTEGRATION)
                        .description("""
                                Estado de la integracion con las demas celulas. \
                                `GET /api/v1/system/integration-status` consulta un \
                                endpoint real de cada modulo y devuelve su estado -UP, \
                                DEGRADED si pasa del umbral de milisegundos, DOWN si \
                                falla- junto al tiempo de respuesta. Cada consulta queda \
                                persistida y es recuperable por el tag `System - Checks`."""),
                new Tag()
                        .name(TAG_CHECKS)
                        .description("""
                                Registro historico de las comprobaciones tecnicas. CRUD \
                                sobre `/api/v1/system/checks`, con filtros por `component` \
                                y por `status` y paginacion. Aqui aterrizan los resultados \
                                que deja `integration-status`, de modo que la integracion \
                                entre modulos queda consultable en la base de datos y no \
                                solo en el momento de la llamada."""));
    }
}
