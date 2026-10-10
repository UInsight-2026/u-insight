package gt.edu.uinsight.analytics.centraltendency.repository;

import gt.edu.uinsight.analytics.centraltendency.exception.AnalyticsResourceNotFoundException;
import gt.edu.uinsight.analytics.centraltendency.exception.GradeDataIntegrationException;
import gt.edu.uinsight.analytics.centraltendency.exception.InvalidAnalyticsRequestException;
import gt.edu.uinsight.analytics.centraltendency.model.GradeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GradeDataRepositoryImplTest {

    private static final String A6_BASE_URL = "http://localhost:8086";
    private static final String A4_BASE_URL = "http://localhost:8084";
    private static final String A1_BASE_URL = "http://localhost:8081";

    private MockRestServiceServer a6Server;
    private MockRestServiceServer a4Server;
    private MockRestServiceServer a1Server;
    private GradeDataRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        RestClient.Builder a6Builder = RestClient.builder().baseUrl(A6_BASE_URL);
        RestClient.Builder a4Builder = RestClient.builder().baseUrl(A4_BASE_URL);
        RestClient.Builder a1Builder = RestClient.builder().baseUrl(A1_BASE_URL);
        a6Server = MockRestServiceServer.bindTo(a6Builder).build();
        a4Server = MockRestServiceServer.bindTo(a4Builder).build();
        a1Server = MockRestServiceServer.bindTo(a1Builder).build();
        repository = new GradeDataRepositoryImpl(
                a6Builder.build(),
                a4Builder.build(),
                a1Builder.build()
        );
    }

    @Test
    void obtainsSectionGradesFromA6AndAppliesTheEvaluationFilter() {
        a4Server.expect(once(), requestTo(A4_BASE_URL + "/api/v1/sections/10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":10,\"academicPeriodId\":2,\"courseId\":5,\"status\":\"ACTIVE\"}",
                        MediaType.APPLICATION_JSON
                ));
        a6Server.expect(once(), requestTo(A6_BASE_URL + "/api/v1/sections/10/grades"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        """
                        [
                          {"id":1,"evaluationId":45,"studentId":101,"score":70,"status":"REGISTERED"},
                          {"id":2,"evaluationId":46,"studentId":102,"score":90,"status":"REGISTERED"}
                        ]
                        """,
                        MediaType.APPLICATION_JSON
                ));

        List<GradeData> result = repository.findBySectionId(10L, 45L);

        assertEquals(1, result.size());
        assertEquals(45L, result.get(0).getEvaluationId());
        assertEquals(5L, result.get(0).getCourseId());
        assertEquals(70.0, result.get(0).getScore().doubleValue());
        verifyServers();
    }

    @Test
    void returnsAnEmptyListWhenA6HasNoGradesForTheSection() {
        expectActiveSection(10L);
        a6Server.expect(once(), requestTo(A6_BASE_URL + "/api/v1/sections/10/grades"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<GradeData> result = repository.findBySectionId(10L, null);

        assertTrue(result.isEmpty());
        verifyServers();
    }

    @Test
    void translatesAnA6ServerFailureIntoAnIntegrationException() {
        expectActiveSection(10L);
        a6Server.expect(once(), requestTo(A6_BASE_URL + "/api/v1/sections/10/grades"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        GradeDataIntegrationException exception = assertThrows(
                GradeDataIntegrationException.class,
                () -> repository.findBySectionId(10L, null)
        );

        assertTrue(exception.getMessage().contains("A6"));
        verifyServers();
    }

    @Test
    void obtainsOnlyTheActiveSectionsForTheRequestedCourseAndPeriod() {
        a1Server.expect(once(), requestTo(A1_BASE_URL + "/api/v1/courses/5"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());
        a1Server.expect(once(), requestTo(A1_BASE_URL + "/api/v1/academic-periods/2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());
        a4Server.expect(once(), requestTo(A4_BASE_URL + "/api/v1/sections"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        """
                        [
                          {"id":10,"academicPeriodId":2,"courseId":5,"status":"ACTIVE"},
                          {"id":11,"academicPeriodId":2,"courseId":5,"status":"CLOSED"},
                          {"id":12,"academicPeriodId":3,"courseId":5,"status":"ACTIVE"},
                          {"id":13,"academicPeriodId":2,"courseId":6,"status":"ACTIVE"}
                        ]
                        """,
                        MediaType.APPLICATION_JSON
                ));
        a6Server.expect(once(), requestTo(A6_BASE_URL + "/api/v1/sections/10/grades"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "[{\"id\":1,\"evaluationId\":45,\"studentId\":101,\"score\":75}]",
                        MediaType.APPLICATION_JSON
                ));

        List<GradeData> result = repository.findByCourseId(5L, 2L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getSectionId());
        verifyServers();
    }

    @Test
    void obtainsGradesForAClosedSection() {
        a4Server.expect(once(), requestTo(A4_BASE_URL + "/api/v1/sections/10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":10,\"academicPeriodId\":2,\"courseId\":5,\"status\":\"CLOSED\"}",
                        MediaType.APPLICATION_JSON
                ));
        a6Server.expect(once(), requestTo(A6_BASE_URL + "/api/v1/sections/10/grades"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "[{\"id\":1,\"evaluationId\":45,\"studentId\":101,\"score\":70}]",
                        MediaType.APPLICATION_JSON
                ));

        List<GradeData> result = repository.findBySectionId(10L, null);

        assertEquals(1, result.size());
        assertEquals(70.0, result.get(0).getScore().doubleValue());
        verifyServers();
    }

    @Test
    void translatesA1CourseNotFoundResponseIntoTheModuleException() {
        a1Server.expect(once(), requestTo(A1_BASE_URL + "/api/v1/courses/99"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        assertThrows(
                AnalyticsResourceNotFoundException.class,
                () -> repository.findByCourseId(99L, null)
        );
        verifyServers();
    }

    @Test
    void rejectsAPeriodThatDoesNotExistInA1() {
        a1Server.expect(once(), requestTo(A1_BASE_URL + "/api/v1/courses/5"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());
        a1Server.expect(once(), requestTo(A1_BASE_URL + "/api/v1/academic-periods/99"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        assertThrows(
                InvalidAnalyticsRequestException.class,
                () -> repository.findByCourseId(5L, 99L)
        );
        verifyServers();
    }

    @Test
    void translatesA4NotFoundResponseIntoTheModuleException() {
        a4Server.expect(once(), requestTo(A4_BASE_URL + "/api/v1/sections/99"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        assertThrows(
                AnalyticsResourceNotFoundException.class,
                () -> repository.findBySectionId(99L, null)
        );
        verifyServers();
    }

    private void expectActiveSection(Long sectionId) {
        a4Server.expect(once(), requestTo(A4_BASE_URL + "/api/v1/sections/" + sectionId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":" + sectionId
                                + ",\"academicPeriodId\":2,\"courseId\":5,\"status\":\"ACTIVE\"}",
                        MediaType.APPLICATION_JSON
                ));
    }

    private void verifyServers() {
        a6Server.verify();
        a4Server.verify();
        a1Server.verify();
    }
}
