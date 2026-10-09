package gt.edu.uinsight.system.config;

import gt.edu.uinsight.system.controller.HealthController;
import gt.edu.uinsight.system.controller.IntegrationStatusController;
import gt.edu.uinsight.system.controller.ReadinessController;
import gt.edu.uinsight.system.controller.SystemCheckController;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Valida el contrato OpenAPI del grupo C7 sin levantar el contexto de Spring.
 *
 * <p>Se afirma sobre el documento que produce el {@code OpenApiCustomizer} del grupo, que
 * es exactamente lo que sirve {@code /v3/api-docs/C7 - system}, y no sobre un bean global:
 * la documentacion del modulo no debe aparecer en los grupos de las demas celulas.
 */
class SystemOpenApiConfigTest {

    private final OpenAPI openAPI = documentoDelGrupo();

    /** El contrato tal como lo sirve el grupo {@code C7 - system} de Swagger UI. */
    private static OpenAPI documentoDelGrupo() {

        OpenAPI openApi = new OpenAPI();
        new SystemOpenApiConfig().documentarModulo(openApi);
        return openApi;
    }

    @Test
    void elInfoDelProyectoEstaCompleto() {

        assertNotNull(openAPI.getInfo(), "Sin Info, Swagger UI cae al titulo generico");

        assertEquals("U-Insight API", openAPI.getInfo().getTitle());
        assertEquals("v1", openAPI.getInfo().getVersion(),
                "La version del contrato debe seguir al prefijo /api/v1 de las rutas");

        assertNotNull(openAPI.getInfo().getDescription());
        assertNotNull(openAPI.getInfo().getContact());

        assertNotNull(openAPI.getExternalDocs());
        assertTrue(openAPI.getExternalDocs().getUrl().contains("github.com/UInsight-2026"));
    }

    @Test
    void losCuatroTagsDelModuloEstanDescritos() {

        assertNotNull(openAPI.getTags());
        assertEquals(4, openAPI.getTags().size(),
                "El modulo expone cuatro endpoints y cada uno lleva su tag");

        openAPI.getTags().forEach(tag -> {

            assertNotNull(tag.getDescription(),
                    "El tag " + tag.getName() + " no tiene descripcion");

            assertTrue(tag.getDescription().length() > 60,
                    "La descripcion del tag " + tag.getName() + " no explica el endpoint: "
                    + tag.getDescription());
        });
    }

    /**
     * El fallo que acecha aqui: si un nombre declarado en el bean deja de coincidir con la
     * anotacion del controlador, springdoc no avisa, muestra el tag dos veces -el del bean,
     * vacio, y el del controlador- y el contrato queda sucio sin que nadie lo note.
     */
    @Test
    void losNombresDeLosTagsCoincidenConLosDeLosControladores() {

        Set<String> enElBean = openAPI.getTags().stream()
                .map(io.swagger.v3.oas.models.tags.Tag::getName)
                .collect(Collectors.toSet());

        Set<String> enLosControladores = Stream.of(
                        HealthController.class,
                        ReadinessController.class,
                        IntegrationStatusController.class,
                        SystemCheckController.class)
                .flatMap(controlador -> Arrays.stream(controlador.getAnnotationsByType(
                        io.swagger.v3.oas.annotations.tags.Tag.class)))
                .map(io.swagger.v3.oas.annotations.tags.Tag::name)
                .collect(Collectors.toSet());

        assertEquals(enLosControladores, enElBean,
                "Los tags declarados en SystemOpenApiConfig deben ser exactamente los que anotan "
                + "los controladores del modulo");
    }

    @Test
    void elOrdenDeLosTagsVaDeLoBaratoALoHistorico() {

        List<String> orden = openAPI.getTags().stream()
                .map(io.swagger.v3.oas.models.tags.Tag::getName)
                .toList();

        assertEquals(
                List.of(
                        SystemOpenApiConfig.TAG_HEALTH,
                        SystemOpenApiConfig.TAG_READINESS,
                        SystemOpenApiConfig.TAG_INTEGRATION,
                        SystemOpenApiConfig.TAG_CHECKS),
                orden,
                "El orden declarado es el que Swagger UI respeta; alfabetico no dice nada");
    }
}
