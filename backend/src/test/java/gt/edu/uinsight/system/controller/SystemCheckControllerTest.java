
package gt.edu.uinsight.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gt.edu.uinsight.system.dto.request.CreateCheckRequest;
import gt.edu.uinsight.system.dto.request.UpdateCheckStatusRequest;
import gt.edu.uinsight.system.dto.response.CheckResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.exception.CheckNotFoundException;
import gt.edu.uinsight.system.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.system.exception.SystemExceptionHandler;
import gt.edu.uinsight.system.logging.SystemEventLogger;
import gt.edu.uinsight.system.service.SystemCheckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SystemCheckController.class,
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        SystemCheckController.class,
                        SystemExceptionHandler.class
                }
        )
)
class SystemCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SystemCheckService service;

    @MockitoBean
    private SystemEventLogger eventLogger;

    private CheckResponse sampleResponse() {
        return new CheckResponse(
                1L,
                "api",
                CheckStatus.UP,
                "Funcionando correctamente",
                LocalDateTime.of(2026, 10, 9, 10, 0)
        );
    }

    // 1. Crear un registro correctamente: HTTP 201.
    @Test
    void createCheck_validRequest_returns201() throws Exception {
        when(service.createCheck(any(CreateCheckRequest.class)))
                .thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/system/checks")
                        .contentType("application/json")
                        .content("""
                                {
                                  "component": "api",
                                  "status": "UP",
                                  "message": "Funcionando correctamente"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.component").value("api"));
    }

    // 2. Falta component: HTTP 400.
    @Test
    void createCheck_withoutComponent_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/system/checks")
                        .contentType("application/json")
                        .content("""
                                {
                                  "status": "UP",
                                  "message": "Prueba"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "component is required")));
    }

    // 3. component supera los 100 caracteres: HTTP 400.
    @Test
    void createCheck_componentTooLong_returns400() throws Exception {
        String component = "a".repeat(101);

        mockMvc.perform(post("/api/v1/system/checks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreateCheckRequest(
                                        component,
                                        CheckStatus.UP,
                                        "Prueba"
                                )
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "component must be at most 100 characters")));
    }

    // 4. Falta status: HTTP 400.
    @Test
    void createCheck_withoutStatus_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/system/checks")
                        .contentType("application/json")
                        .content("""
                                {
                                  "component": "api",
                                  "message": "Prueba"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "status is required")));
    }

    // 5. message supera los 500 caracteres: HTTP 400.
    @Test
    void createCheck_messageTooLong_returns400() throws Exception {
        String message = "m".repeat(501);

        mockMvc.perform(post("/api/v1/system/checks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreateCheckRequest(
                                        "api",
                                        CheckStatus.UP,
                                        message
                                )
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "message must be at most 500 characters")));
    }

    // 6. Consultar un registro inexistente: HTTP 404.
    @Test
    void getCheckById_notFound_returns404() throws Exception {
        when(service.getCheckById(999L))
                .thenThrow(new CheckNotFoundException(999L));

        mockMvc.perform(get("/api/v1/system/checks/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "System check with id 999 not found")));
    }

    // 7. Cambiar de DOWN directamente a UP: HTTP 409.
    @Test
    void updateStatus_downToUp_returns409() throws Exception {
        when(service.updateStatus(
                eq(1L),
                any(UpdateCheckStatusRequest.class)
        )).thenThrow(
                new InvalidStatusTransitionException(
                        CheckStatus.DOWN,
                        CheckStatus.UP
                )
        );

        mockMvc.perform(patch("/api/v1/system/checks/1/status")
                        .contentType("application/json")
                        .content("""
                                {
                                  "status": "UP"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "a check in DOWN must go through DEGRADED first")));
    }

    // 8. Los filtros llegan al servicio.
    @Test
    void getChecks_withFilters_passesValuesToService() throws Exception {
        when(service.getChecks(
                eq("api"),
                eq(CheckStatus.UP),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/system/checks")
                        .param("component", "api")
                        .param("status", "UP"))
                .andExpect(status().isOk());

        verify(service).getChecks(
                eq("api"),
                eq(CheckStatus.UP),
                any(Pageable.class)
        );
    }

    // 9. Se respeta la paginación personalizada.
    @Test
    void getChecks_customPageAndSize_passesPaginationToService()
            throws Exception {

        when(service.getChecks(
                eq("api"),
                eq(CheckStatus.UP),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/system/checks")
                        .param("component", "api")
                        .param("status", "UP")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk());

        var captor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(service).getChecks(
                eq("api"),
                eq(CheckStatus.UP),
                captor.capture()
        );

        assertEquals(2, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
    }

    // 10. Valores predeterminados de paginación: page=0 y size=10.
    @Test
    void getChecks_withoutPagination_usesDefaultPageAndSize()
            throws Exception {

        when(service.getChecks(
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/system/checks"))
                .andExpect(status().isOk());

        var captor =
                org.mockito.ArgumentCaptor.forClass(Pageable.class);

        verify(service).getChecks(
                eq(null),
                eq(null),
                captor.capture()
        );

        assertEquals(0, captor.getValue().getPageNumber());
        assertEquals(10, captor.getValue().getPageSize());
    }
}
