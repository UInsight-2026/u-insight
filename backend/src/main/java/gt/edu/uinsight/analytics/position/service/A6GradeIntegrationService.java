package gt.edu.uinsight.analytics.position.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementacion real de GradeIntegrationService que consume por HTTP la API
 * de A6 (calificaciones). Se activa solo bajo el perfil "prod" para no entrar
 * en conflicto de beans con MockGradeIntegrationService.
 *
 * Endpoints de A6 usados:
 * - GET /api/v1/sections/{id}/grades
 * - GET /api/v1/students/{id}/grades
 * - GET /api/v1/grades/evaluation-refs   (nota maxima de cada evaluacion)
 * - GET /api/v1/grades/enrollment-refs   (matriculas estudiante-seccion)
 */
@Service
@Profile("prod")
public class A6GradeIntegrationService implements GradeIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(A6GradeIntegrationService.class);

    private static final ParameterizedTypeReference<List<A6Grade>> GRADES_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<A6EvaluationRef>> EVALUATION_REFS_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<A6Enrollment>> ENROLLMENTS_TYPE = new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    @Autowired
    public A6GradeIntegrationService(@Value("${uinsight.integration.a6.base-url:http://localhost:8080}") String a6BaseUrl) {
        this(RestClient.builder().baseUrl(a6BaseUrl).build());
    }

    A6GradeIntegrationService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Double> getGradesBySection(Long sectionId) {
        log.info("Consultando notas de A6 para la seccion {}", sectionId);
        return normalizeToHundred(get("/api/v1/sections/{id}/grades", GRADES_TYPE, sectionId));
    }

    @Override
    public List<Double> getGradesByStudent(Long studentId) {
        log.info("Consultando notas de A6 para el estudiante {}", studentId);
        return normalizeToHundred(get("/api/v1/students/{id}/grades", GRADES_TYPE, studentId));
    }

    @Override
    public List<Long> getSectionIdsByStudent(Long studentId) {
        log.info("Consultando matriculas de A6 para el estudiante {}", studentId);
        return fetchEnrollments().stream()
                .filter(enrollment -> studentId.equals(enrollment.studentId()))
                .map(A6Enrollment::sectionId)
                .distinct()
                .toList();
    }

    @Override
    public boolean isStudentEnrolledInSection(Long studentId, Long sectionId) {
        return fetchEnrollments().stream()
                .anyMatch(enrollment -> studentId.equals(enrollment.studentId())
                        && sectionId.equals(enrollment.sectionId()));
    }

    private List<A6Enrollment> fetchEnrollments() {
        return get("/api/v1/grades/enrollment-refs", ENROLLMENTS_TYPE);
    }

    /**
     * Normaliza cada nota a una escala de 0-100 usando la nota maxima real de su
     * evaluacion. Las notas cuya evaluacion no tiene nota maxima valida se descartan
     * y se registra un WARN, en vez de fallar la consulta completa.
     */
    private List<Double> normalizeToHundred(List<A6Grade> grades) {
        if (grades.isEmpty()) {
            return List.of();
        }
        Map<Long, BigDecimal> maximumScoreByEvaluation = get("/api/v1/grades/evaluation-refs", EVALUATION_REFS_TYPE)
                .stream()
                .collect(Collectors.toMap(A6EvaluationRef::id, A6EvaluationRef::maximumScore, (a, b) -> a));

        return grades.stream()
                .map(grade -> {
                    BigDecimal maximumScore = maximumScoreByEvaluation.get(grade.evaluationId());
                    if (maximumScore == null || maximumScore.compareTo(BigDecimal.ZERO) == 0) {
                        log.warn("Se descarta la nota {} (evaluacion {}): sin nota maxima valida",
                                grade.id(), grade.evaluationId());
                        return null;
                    }
                    return grade.score()
                            .divide(maximumScore, 6, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private <T> List<T> get(String uri, ParameterizedTypeReference<List<T>> type, Object... uriVariables) {
        try {
            List<T> body = restClient.get()
                    .uri(uri, uriVariables)
                    .retrieve()
                    .body(type);
            return body == null ? List.of() : body;
        } catch (RestClientException ex) {
            log.error("Fallo la llamada a A6 ({}): {}", uri, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo obtener informacion del modulo de calificaciones (A6)");
        }
    }

    // Contratos de respuesta de A6. Solo se leen los campos que B2 necesita.
    private record A6Grade(Long id, Long evaluationId, Long studentId, BigDecimal score) {}

    private record A6EvaluationRef(Long id, Long sectionId, BigDecimal maximumScore) {}

    private record A6Enrollment(Long id, Long studentId, Long sectionId) {}
}
