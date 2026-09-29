package gt.edu.uinsight.analytics.centraltendency.repository;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import gt.edu.uinsight.analytics.centraltendency.dto.integration.GradeApiResponse;
import gt.edu.uinsight.analytics.centraltendency.dto.integration.SectionApiResponse;
import gt.edu.uinsight.analytics.centraltendency.model.GradeData;

/**
 * Obtiene las calificaciones consumiendo por HTTP a A6 (grades), y valida
 * seccion/curso/periodo contra A4 (sections) y A1 (courses, academic-periods).
 */
@Repository
public class GradeDataRepositoryImpl implements GradeDataRepository {

    private static final String ACTIVE = "ACTIVE";

    private final RestClient gradesClient;
    private final RestClient sectionsClient;
    private final RestClient academicClient;

    @Autowired
    public GradeDataRepositoryImpl(
            @Value("${uinsight.integration.a6.base-url:http://localhost:8080}") String a6BaseUrl,
            @Value("${uinsight.integration.a4.base-url:http://localhost:8080}") String a4BaseUrl,
            @Value("${uinsight.integration.a1.base-url:http://localhost:8080}") String a1BaseUrl) {
        this(RestClient.builder().baseUrl(a6BaseUrl).build(),
                RestClient.builder().baseUrl(a4BaseUrl).build(),
                RestClient.builder().baseUrl(a1BaseUrl).build());
    }

    GradeDataRepositoryImpl(RestClient gradesClient, RestClient sectionsClient, RestClient academicClient) {
        this.gradesClient = gradesClient;
        this.sectionsClient = sectionsClient;
        this.academicClient = academicClient;
    }

    @Override
    public List<GradeData> findBySectionId(Long sectionId, Long evaluationId) {
        SectionApiResponse section = execute("A4", HttpStatus.NOT_FOUND,
                "La seccion " + sectionId + " no existe",
                () -> sectionsClient.get()
                        .uri("/api/v1/sections/{id}", sectionId)
                        .retrieve()
                        .body(SectionApiResponse.class));
        if (section == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La seccion " + sectionId + " no existe");
        }
        return fetchGrades(section, evaluationId);
    }

    @Override
    public List<GradeData> findByCourseId(Long courseId, Long periodId) {
        execute("A1", HttpStatus.NOT_FOUND, "El curso " + courseId + " no existe",
                () -> academicClient.get()
                        .uri("/api/v1/courses/{id}", courseId)
                        .retrieve()
                        .toBodilessEntity());

                if (periodId != null) {
                    execute("A1", HttpStatus.BAD_REQUEST, "El periodo academico " + periodId + " no es valido",
                        () -> academicClient.get()
                            .uri("/api/v1/academic-periods/{id}", periodId)
                            .retrieve()
                            .toBodilessEntity());
                }

        List<SectionApiResponse> sections = execute("A4", HttpStatus.NOT_FOUND,
                "No se pudo listar las secciones",
                () -> sectionsClient.get()
                        .uri("/api/v1/sections")
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<SectionApiResponse>>() { }));
        if (sections == null) {
            return List.of();
        }

        return sections.stream()
                .filter(section -> Objects.equals(section.courseId(), courseId))
            .filter(section -> periodId == null || Objects.equals(section.academicPeriodId(), periodId))
                .filter(section -> ACTIVE.equalsIgnoreCase(section.status()))
                .flatMap(section -> fetchGrades(section, null).stream())
                .toList();
    }

    private List<GradeData> fetchGrades(SectionApiResponse section, Long evaluationId) {
        List<GradeApiResponse> grades = execute("A6", HttpStatus.NOT_FOUND,
                "No se encontraron calificaciones para la seccion " + section.id(),
                () -> gradesClient.get()
                        .uri("/api/v1/sections/{id}/grades", section.id())
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<GradeApiResponse>>() { }));
        if (grades == null) {
            return List.of();
        }

        return grades.stream()
                .filter(grade -> grade.score() != null)
                .filter(grade -> evaluationId == null || Objects.equals(grade.evaluationId(), evaluationId))
                .map(grade -> new GradeData(grade.id(), grade.evaluationId(), grade.studentId(),
                        section.id(), section.courseId(), grade.score()))
                .toList();
    }

    private <T> T execute(String service, HttpStatus notFoundStatus, String notFoundMessage, Supplier<T> call) {
        try {
            return call.get();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(notFoundStatus, notFoundMessage);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar el servicio " + service);
        }
    }
}
