package gt.edu.uinsight.analytics.centraltendency.controller;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.centraltendency.exception.AnalyticsResourceNotFoundException;
import gt.edu.uinsight.analytics.centraltendency.exception.GlobalAnalyticsExceptionHandler;
import gt.edu.uinsight.analytics.centraltendency.exception.GradeDataIntegrationException;
import gt.edu.uinsight.analytics.centraltendency.exception.InvalidAnalyticsRequestException;
import gt.edu.uinsight.analytics.centraltendency.service.CentralTendencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    @Mock
    private CentralTendencyService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AnalyticsController(service))
                .setControllerAdvice(new GlobalAnalyticsExceptionHandler())
                .build();
    }

    @Test
    void returnsSectionCentralTendencyUsingTheEvaluationFilter() throws Exception {
        when(service.getSectionCentralTendency(10L, 45L)).thenReturn(
                new CentralTendencyResponse(3, 80.0, 80.0, List.of(80.0))
        );

        mockMvc.perform(get("/api/v1/analytics/sections/10/central-tendency")
                        .queryParam("evaluationId", "45"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sampleSize").value(3))
                .andExpect(jsonPath("$.mean").value(80.0))
                .andExpect(jsonPath("$.median").value(80.0))
                .andExpect(jsonPath("$.mode[0]").value(80.0));

        verify(service).getSectionCentralTendency(10L, 45L);
    }

    @Test
    void returnsCourseCentralTendencyUsingThePeriodFilter() throws Exception {
        when(service.getCourseCentralTendency(5L, 2L)).thenReturn(
                new CentralTendencyResponse(2, 75.0, 75.0, List.of())
        );

        mockMvc.perform(get("/api/v1/analytics/courses/5/central-tendency")
                        .queryParam("periodId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sampleSize").value(2))
                .andExpect(jsonPath("$.mean").value(75.0))
                .andExpect(jsonPath("$.mode").isEmpty());

        verify(service).getCourseCentralTendency(5L, 2L);
    }

    @Test
    void returnsHttp200WithTheStableEmptyContractWhenThereAreNoGrades() throws Exception {
        when(service.getSectionCentralTendency(10L, null))
                .thenReturn(CentralTendencyResponse.empty());

        mockMvc.perform(get("/api/v1/analytics/sections/10/central-tendency"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sampleSize").value(0))
                .andExpect(jsonPath("$.mean").value(nullValue()))
                .andExpect(jsonPath("$.median").value(nullValue()))
                .andExpect(jsonPath("$.mode").isEmpty());
    }

    @Test
    void returnsHttp400WhenAPathIdentifierIsNotNumeric() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/sections/abc/central-tendency"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.traceId").isNotEmpty());
    }

    @Test
    void returnsHttp400WhenTheServiceRejectsABusinessRule() throws Exception {
        when(service.getCourseCentralTendency(5L, 0L)).thenThrow(
                new InvalidAnalyticsRequestException("periodId debe ser un numero mayor que cero")
        );

        mockMvc.perform(get("/api/v1/analytics/courses/5/central-tendency")
                        .queryParam("periodId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("periodId debe ser un numero mayor que cero"));
    }

    @Test
    void returnsHttp404WhenTheRequestedResourceDoesNotExist() throws Exception {
        when(service.getSectionCentralTendency(99L, null)).thenThrow(
                new AnalyticsResourceNotFoundException("Seccion no encontrada: 99")
        );

        mockMvc.perform(get("/api/v1/analytics/sections/99/central-tendency")
                        .header("X-Trace-Id", "b1-test-trace"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Seccion no encontrada: 99"))
                .andExpect(jsonPath("$.traceId").value("b1-test-trace"));
    }

    @Test
    void returnsHttp502WhenADependencyIsUnavailable() throws Exception {
        when(service.getSectionCentralTendency(10L, null)).thenThrow(
                new GradeDataIntegrationException(
                        "No fue posible consultar la informacion de la celula A6"
                )
        );

        mockMvc.perform(get("/api/v1/analytics/sections/10/central-tendency"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("INTEGRATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("No fue posible consultar la informacion de la celula A6"));
    }

    @Test
    void returnsHttp500WithoutExposingTheUnexpectedException() throws Exception {
        when(service.getCourseCentralTendency(5L, null)).thenThrow(
                new IllegalStateException("detalle interno que no debe exponerse")
        );

        mockMvc.perform(get("/api/v1/analytics/courses/5/central-tendency"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("Ocurrio un error inesperado al procesar la solicitud"));
    }
}
