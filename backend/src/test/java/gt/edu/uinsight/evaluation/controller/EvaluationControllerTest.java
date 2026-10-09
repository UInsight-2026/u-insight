// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.controller;

import gt.edu.uinsight.evaluation.dto.request.ChangeEvaluationStatusRequest;
import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.request.UpdateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.exception.EvaluationExceptionHandler;
import gt.edu.uinsight.evaluation.exception.EvaluationNotEditableException;
import gt.edu.uinsight.evaluation.exception.EvaluationNotFoundException;
import gt.edu.uinsight.evaluation.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.evaluation.exception.SectionNotActiveException;
import gt.edu.uinsight.evaluation.exception.SectionNotFoundException;
import gt.edu.uinsight.evaluation.service.EvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa HTTP de A5: códigos de estado y formato de error
 * { timestamp, status, error, message, details, traceId }.
 */
class EvaluationControllerTest {

    private EvaluationService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(EvaluationService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new EvaluationController(service))
                .setControllerAdvice(new EvaluationExceptionHandler())
                .build();
    }

    private static EvaluationResponse response(String status) {
        return new EvaluationResponse(1L, 1L, "Examen Parcial 1", "EXAM", LocalDate.of(2026, 10, 15),
                new BigDecimal("100"), new BigDecimal("30"), status);
    }

    private static final String VALID_CREATE = """
            {"sectionId":1,"name":"Examen Parcial 1","type":"EXAM",
             "evaluationDate":"2026-10-15","maximumScore":100,"weight":30}
            """;

    private static final String VALID_UPDATE = """
            {"name":"Examen Parcial 1","evaluationDate":"2026-10-20","maximumScore":100,"weight":30}
            """;

    @Test
    @DisplayName("POST válido -> 201 con la evaluación en DRAFT")
    void create_returns201() throws Exception {
        when(service.createEvaluation(any(CreateEvaluationRequest.class))).thenReturn(response("DRAFT"));

        mockMvc.perform(post("/api/v1/evaluations").contentType(MediaType.APPLICATION_JSON).content(VALID_CREATE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @DisplayName("POST con datos inválidos -> 400 VALIDATION_ERROR con detalle por campo")
    void create_invalid_returns400() throws Exception {
        String body = """
                {"sectionId":1,"name":"","type":"ESSAY","evaluationDate":"2026-10-15",
                 "maximumScore":0,"weight":150}
                """;

        mockMvc.perform(post("/api/v1/evaluations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details", hasSize(4)))
                .andExpect(jsonPath("$.details", hasItem("weight: La ponderación no puede ser mayor a 100")))
                .andExpect(jsonPath("$.traceId").exists());
        verify(service, never()).createEvaluation(any());
    }

    @Test
    @DisplayName("POST con JSON mal formado -> 400 MALFORMED_REQUEST")
    void create_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/evaluations").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sectionId\":1,\"evaluationDate\":\"15/10/2026\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MALFORMED_REQUEST"));
    }

    @Test
    @DisplayName("POST sobre sección inexistente -> 404 SECTION_NOT_FOUND")
    void create_sectionNotFound_returns404() throws Exception {
        when(service.createEvaluation(any())).thenThrow(new SectionNotFoundException(1L));

        mockMvc.perform(post("/api/v1/evaluations").contentType(MediaType.APPLICATION_JSON).content(VALID_CREATE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SECTION_NOT_FOUND"));
    }

    @Test
    @DisplayName("POST sobre sección inactiva -> 422 SECTION_NOT_ACTIVE")
    void create_sectionNotActive_returns422() throws Exception {
        when(service.createEvaluation(any())).thenThrow(new SectionNotActiveException(1L));

        mockMvc.perform(post("/api/v1/evaluations").contentType(MediaType.APPLICATION_JSON).content(VALID_CREATE))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("SECTION_NOT_ACTIVE"));
    }

    @Test
    @DisplayName("GET por id inexistente -> 404 EVALUATION_NOT_FOUND")
    void getById_notFound_returns404() throws Exception {
        when(service.getEvaluationById(9L)).thenThrow(new EvaluationNotFoundException(9L));

        mockMvc.perform(get("/api/v1/evaluations/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("EVALUATION_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET con id no numérico -> 400")
    void getById_badId_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/evaluations/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("GET evaluaciones de una sección sin evaluaciones -> 200 []")
    void getBySection_empty_returns200() throws Exception {
        when(service.getEvaluationsBySectionId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/sections/1/evaluations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("PUT sobre evaluación CLOSED -> 409 EVALUATION_NOT_EDITABLE")
    void update_closed_returns409() throws Exception {
        when(service.updateEvaluation(eq(1L), any(UpdateEvaluationRequest.class)))
                .thenThrow(new EvaluationNotEditableException(1L, "CLOSED"));

        mockMvc.perform(put("/api/v1/evaluations/1").contentType(MediaType.APPLICATION_JSON).content(VALID_UPDATE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("EVALUATION_NOT_EDITABLE"));
    }

    @Test
    @DisplayName("PATCH con estado inexistente -> 400 VALIDATION_ERROR")
    void changeStatus_unknownStatus_returns400() throws Exception {
        mockMvc.perform(patch("/api/v1/evaluations/1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"HOLA\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
        verify(service, never()).changeStatus(any(), any());
    }

    @Test
    @DisplayName("PATCH con transición no permitida -> 409 INVALID_STATUS_TRANSITION")
    void changeStatus_invalidTransition_returns409() throws Exception {
        when(service.changeStatus(eq(1L), any(ChangeEvaluationStatusRequest.class)))
                .thenThrow(new InvalidStatusTransitionException("No se permite cambiar de CLOSED a ACTIVE"));

        mockMvc.perform(patch("/api/v1/evaluations/1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("INVALID_STATUS_TRANSITION"));
    }
}
