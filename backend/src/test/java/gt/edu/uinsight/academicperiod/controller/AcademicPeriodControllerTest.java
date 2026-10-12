package gt.edu.uinsight.academicperiod.controller;

import gt.edu.uinsight.academicperiod.dto.request.CreateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.entity.PeriodStatus;
import gt.edu.uinsight.academicperiod.service.AcademicPeriodService;
import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.academicperiod.support.exception.AcademicBusinessRuleException;
import gt.edu.uinsight.academicperiod.support.exception.AcademicExceptionHandler;
import gt.edu.uinsight.academicperiod.support.exception.AcademicResourceNotFoundException;
import gt.edu.uinsight.academicperiod.support.logging.AcademicEventLogger;
import gt.edu.uinsight.academicperiod.support.logging.AcademicRequestInterceptor;
import gt.edu.uinsight.academicperiod.support.logging.AcademicWebConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del controlador de periodos con MockMvc. Solo se cargan clases de A1:
 * el servicio es un mock y no se usa base de datos.
 */
@WebMvcTest(
        controllers = AcademicPeriodController.class,
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                AcademicPeriodController.class,
                AcademicExceptionHandler.class,
                AcademicEventLogger.class,
                AcademicRequestInterceptor.class,
                AcademicWebConfig.class
        }))
class AcademicPeriodControllerTest {

    private static final String BASE = "/api/v1/academic-periods";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AcademicPeriodService service;

    private static AcademicPeriodResponse response(Long id, PeriodStatus status) {
        return new AcademicPeriodResponse(id, "Primer Semestre", 2026,
                LocalDate.of(2026, 1, 15), LocalDate.of(2026, 5, 30), status,
                LocalDateTime.of(2026, 1, 5, 9, 12));
    }

    @Test
    @DisplayName("POST crea un periodo y responde 201")
    void create_responde201() throws Exception {
        when(service.create(any(CreateAcademicPeriodRequest.class))).thenReturn(response(1L, PeriodStatus.PLANNED));

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Primer Semestre","year":2026,"startDate":"2026-01-15","endDate":"2026-05-30"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("X-Trace-Id", notNullValue()))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PLANNED"))
                .andExpect(jsonPath("$.startDate").value("2026-01-15"));
    }

    @Test
    @DisplayName("POST con campos faltantes responde 400 VALIDATION_ERROR con detalles y traceId")
    void create_responde400ConDetalles() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"","year":2026}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details", hasItem(startsWith("startDate"))))
                .andExpect(jsonPath("$.traceId").value(startsWith("REQ-")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("POST con fechas invertidas responde 400 (RN-02)")
    void create_responde400PorRN02() throws Exception {
        when(service.create(any())).thenThrow(AcademicBusinessRuleException.badRequest("RN-02",
                "La fecha de inicio debe ser estrictamente anterior a la fecha de fin"));

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Primer Semestre","year":2026,"startDate":"2026-06-01","endDate":"2026-01-01"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value("rule: RN-02"));
    }

    @Test
    @DisplayName("POST con nombre repetido en el anio responde 409 (RN-07)")
    void create_responde409PorRN07() throws Exception {
        when(service.create(any())).thenThrow(AcademicBusinessRuleException.duplicate("RN-07", "Duplicado"));

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Primer Semestre","year":2026,"startDate":"2026-01-15","endDate":"2026-05-30"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    @DisplayName("GET /{id} inexistente responde 404 NOT_FOUND")
    void findById_responde404() throws Exception {
        when(service.findById(999L)).thenThrow(
                new AcademicResourceNotFoundException("No existe un periodo academico con id 999"));

        mockMvc.perform(get(BASE + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("No existe un periodo academico con id 999"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("GET /{id} con id no numerico responde 400")
    void findById_responde400PorIdInvalido() throws Exception {
        mockMvc.perform(get(BASE + "/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("GET lista paginada con filtro de estado responde 200")
    void findAll_responde200Paginado() throws Exception {
        when(service.findAll(eq("ACTIVE"), any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(response(1L, PeriodStatus.ACTIVE)), 0, 5, 1, 1));

        mockMvc.perform(get(BASE).param("status", "ACTIVE").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET lista sin filtro usa page=0 y size=20 por defecto")
    void findAll_usaPaginacionPorDefecto() throws Exception {
        when(service.findAll(isNull(), any(Pageable.class))).thenReturn(new PageResponse<>(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get(BASE)).andExpect(status().isOk());

        verify(service).findAll(isNull(), eq(PageRequest.of(0, 20,
                Sort.by("id").ascending())));
    }

    @Test
    @DisplayName("GET /active responde 200 con el periodo activo")
    void findActive_responde200() throws Exception {
        when(service.findActive()).thenReturn(response(1L, PeriodStatus.ACTIVE));

        mockMvc.perform(get(BASE + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("PUT de un periodo CLOSED responde 409 (RN-04)")
    void update_responde409PorRN04() throws Exception {
        when(service.update(eq(1L), any())).thenThrow(AcademicBusinessRuleException.conflict("RN-04", "Cerrado"));

        mockMvc.perform(put(BASE + "/1").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Primer Semestre","startDate":"2026-01-15","endDate":"2026-05-30"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.details[0]").value("rule: RN-04"));
    }

    @Test
    @DisplayName("PATCH /{id}/status cambia el estado y responde 200")
    void changeStatus_responde200() throws Exception {
        when(service.changeStatus(eq(1L), eq(new ChangeStatusRequest("ACTIVE"))))
                .thenReturn(response(1L, PeriodStatus.ACTIVE));

        mockMvc.perform(patch(BASE + "/1/status").contentType(MediaType.APPLICATION_JSON).content("""
                        {"status":"ACTIVE"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("PATCH /{id}/status con segundo ACTIVE responde 409 (RN-03)")
    void changeStatus_responde409PorRN03() throws Exception {
        when(service.changeStatus(eq(2L), any())).thenThrow(AcademicBusinessRuleException.conflict("RN-03", "Ya hay un activo"));

        mockMvc.perform(patch(BASE + "/2/status").contentType(MediaType.APPLICATION_JSON).content("""
                        {"status":"ACTIVE"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.details[0]").value("rule: RN-03"));
    }

    @Test
    @DisplayName("RN-05: DELETE no esta expuesto y responde 405")
    void delete_noEstaPermitido() throws Exception {
        mockMvc.perform(delete(BASE + "/1")).andExpect(status().isMethodNotAllowed());
    }
}
