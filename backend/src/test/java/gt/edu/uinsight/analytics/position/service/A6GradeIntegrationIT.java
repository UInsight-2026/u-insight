package gt.edu.uinsight.analytics.position.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Prueba de integracion real entre B2 y A6: levanta A6 (calificaciones) y B2 (posicion)
 * con su base H2 de test, carga datos con los endpoints reales de A6 y consulta B2 por HTTP.
 * No requiere MySQL ni credenciales.
 *
 * El contexto se limita a los modulos necesarios porque el contexto completo de la
 * aplicacion no arranca por un conflicto de entidades en el modulo de dispersion.
 */
@SpringBootTest(
        classes = A6GradeIntegrationIT.IntegrationContext.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=18089",
                "uinsight.integration.a6.base-url=http://localhost:18089",
                "spring.profiles.active=prod"
        })
class A6GradeIntegrationIT {

    private static final long SECTION_ID = 9100L;
    private static final long STUDENT_ID = 9500L;
    private static final long EVAL_PARCIAL = 9001L;
    private static final long EVAL_FINAL = 9002L;

    @Autowired
    private GradeIntegrationService gradeIntegrationService;

    @Test
    void usesTheRealA6ImplementationInProdProfile() {
        assertThat(gradeIntegrationService).isInstanceOf(A6GradeIntegrationService.class);
    }

    @Test
    void readsGradesFromRealA6AndExposesThemThroughPositionEndpoint() {
        RestClient client = RestClient.builder().baseUrl("http://localhost:18089").build();

        post(client, "/api/v1/grades/enrollment-refs",
                Map.of("studentId", STUDENT_ID, "sectionId", SECTION_ID));
        post(client, "/api/v1/grades/evaluation-refs",
                Map.of("id", EVAL_PARCIAL, "sectionId", SECTION_ID, "name", "Parcial", "maximumScore", new BigDecimal("20")));
        post(client, "/api/v1/grades/evaluation-refs",
                Map.of("id", EVAL_FINAL, "sectionId", SECTION_ID, "name", "Final", "maximumScore", new BigDecimal("50")));
        post(client, "/api/v1/grades",
                Map.of("evaluationId", EVAL_PARCIAL, "studentId", STUDENT_ID, "score", new BigDecimal("15")));
        post(client, "/api/v1/grades",
                Map.of("evaluationId", EVAL_FINAL, "studentId", STUDENT_ID, "score", new BigDecimal("40")));

        assertThat(gradeIntegrationService.getGradesBySection(SECTION_ID))
                .containsExactlyInAnyOrder(75.0, 80.0);
        assertThat(gradeIntegrationService.getSectionIdsByStudent(STUDENT_ID)).contains(SECTION_ID);
        assertThat(gradeIntegrationService.isStudentEnrolledInSection(STUDENT_ID, SECTION_ID)).isTrue();

        Map<?, ?> position = client.get()
                .uri("/api/v1/analytics/sections/{id}/position", SECTION_ID)
                .retrieve()
                .body(Map.class);

        assertThat(position).isNotNull();
        assertThat(((Number) position.get("sampleSize")).intValue()).isEqualTo(2);

        Map<String, Object> withPercentiles = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/analytics/sections/{id}/position")
                        .queryParam("percentiles", "25,50,75,90")
                        .build(SECTION_ID))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertThat(withPercentiles.get("percentiles"))
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsKeys("P25", "P50", "P75", "P90");
    }

    private void post(RestClient client, String uri, Object body) {
        client.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    /** Contexto minimo: modulo de calificaciones de A6 y modulo de posicion de B2. */
    @SpringBootApplication(scanBasePackages = {
            "gt.edu.uinsight.grade",
            "gt.edu.uinsight.analytics.position"
    })
    @EntityScan(basePackages = "gt.edu.uinsight.grade.model")
    @EnableJpaRepositories(basePackages = "gt.edu.uinsight.grade.repository")
    static class IntegrationContext {
    }
}
