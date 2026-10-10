package gt.edu.uinsight.analytics.position.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class A6GradeIntegrationServiceTest {

    private static final String BASE = "http://a6";

    private RestClient.Builder builder;
    private MockRestServiceServer server;
    private A6GradeIntegrationService service;

    @BeforeEach
    void setUp() {
        builder = RestClient.builder().baseUrl(BASE);
        server = MockRestServiceServer.bindTo(builder).build();
        service = new A6GradeIntegrationService(builder.build());
    }

    @Test
    void getGradesBySectionNormalizesEachScoreToHundredUsingEvaluationMaximum() {
        server.expect(requestTo(BASE + "/api/v1/sections/10/grades"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id":1,"evaluationId":100,"studentId":5,"score":15,"status":"REGISTERED"},
                         {"id":2,"evaluationId":200,"studentId":5,"score":40,"status":"REGISTERED"}]
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/api/v1/grades/evaluation-refs"))
                .andRespond(withSuccess("""
                        [{"id":100,"sectionId":10,"name":"Parcial","maximumScore":20},
                         {"id":200,"sectionId":10,"name":"Final","maximumScore":50}]
                        """, MediaType.APPLICATION_JSON));

        List<Double> grades = service.getGradesBySection(10L);

        assertThat(grades).containsExactly(75.0, 80.0);
        server.verify();
    }

    @Test
    void getGradesByStudentDiscardsGradesWithoutValidEvaluationReference() {
        server.expect(requestTo(BASE + "/api/v1/students/5/grades"))
                .andRespond(withSuccess("""
                        [{"id":1,"evaluationId":100,"studentId":5,"score":10,"status":"REGISTERED"},
                         {"id":2,"evaluationId":999,"studentId":5,"score":10,"status":"REGISTERED"}]
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE + "/api/v1/grades/evaluation-refs"))
                .andRespond(withSuccess("""
                        [{"id":100,"sectionId":10,"name":"Parcial","maximumScore":20}]
                        """, MediaType.APPLICATION_JSON));

        assertThat(service.getGradesByStudent(5L)).containsExactly(50.0);
    }

    @Test
    void getSectionIdsByStudentReturnsDistinctSectionsOfThatStudentOnly() {
        server.expect(requestTo(BASE + "/api/v1/grades/enrollment-refs"))
                .andRespond(withSuccess("""
                        [{"id":1,"studentId":5,"sectionId":10},
                         {"id":2,"studentId":5,"sectionId":11},
                         {"id":3,"studentId":5,"sectionId":10},
                         {"id":4,"studentId":6,"sectionId":12}]
                        """, MediaType.APPLICATION_JSON));

        assertThat(service.getSectionIdsByStudent(5L)).containsExactly(10L, 11L);
    }

    @Test
    void isStudentEnrolledInSectionChecksBothStudentAndSection() {
        server.expect(requestTo(BASE + "/api/v1/grades/enrollment-refs"))
                .andRespond(withSuccess("""
                        [{"id":1,"studentId":5,"sectionId":10}]
                        """, MediaType.APPLICATION_JSON));

        assertThat(service.isStudentEnrolledInSection(5L, 10L)).isTrue();
    }

    @Test
    void isStudentEnrolledInSectionReturnsFalseWhenNotEnrolled() {
        server.expect(requestTo(BASE + "/api/v1/grades/enrollment-refs"))
                .andRespond(withSuccess("""
                        [{"id":1,"studentId":5,"sectionId":10}]
                        """, MediaType.APPLICATION_JSON));

        assertThat(service.isStudentEnrolledInSection(5L, 11L)).isFalse();
    }

    @Test
    void a6ErrorIsReportedAsServiceUnavailable() {
        server.expect(requestTo(BASE + "/api/v1/sections/10/grades"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> service.getGradesBySection(10L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
