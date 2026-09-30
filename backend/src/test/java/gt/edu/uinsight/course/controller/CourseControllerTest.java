package gt.edu.uinsight.course.controller;

import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.academicperiod.support.exception.AcademicBusinessRuleException;
import gt.edu.uinsight.academicperiod.support.exception.AcademicExceptionHandler;
import gt.edu.uinsight.academicperiod.support.exception.AcademicResourceNotFoundException;
import gt.edu.uinsight.academicperiod.support.logging.AcademicEventLogger;
import gt.edu.uinsight.academicperiod.support.logging.AcademicRequestInterceptor;
import gt.edu.uinsight.academicperiod.support.logging.AcademicWebConfig;
import gt.edu.uinsight.course.dto.request.CreateCourseRequest;
import gt.edu.uinsight.course.dto.response.CourseResponse;
import gt.edu.uinsight.course.entity.CourseStatus;
import gt.edu.uinsight.course.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del controlador de cursos con MockMvc. Solo se cargan clases de A1:
 * el servicio es un mock y no se usa base de datos.
 */
@WebMvcTest(
        controllers = CourseController.class,
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                CourseController.class,
                AcademicExceptionHandler.class,
                AcademicEventLogger.class,
                AcademicRequestInterceptor.class,
                AcademicWebConfig.class
        }))
class CourseControllerTest {

    private static final String BASE = "/api/v1/courses";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService service;

    private static CourseResponse response(CourseStatus status) {
        return new CourseResponse(7L, "PROG-II", "Programacion II", "Curso de POO y APIs REST", 5, status,
                LocalDateTime.of(2026, 1, 5, 9, 12));
    }

    @Test
    @DisplayName("POST crea un curso y responde 201 en estado ACTIVE")
    void create_responde201() throws Exception {
        when(service.create(any(CreateCourseRequest.class))).thenReturn(response(CourseStatus.ACTIVE));

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"code":"PROG-II","name":"Programacion II","description":"Curso de POO y APIs REST","credits":5}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.code").value("PROG-II"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST con codigo duplicado responde 409 CONFLICT (RN-01)")
    void create_responde409PorCodigoDuplicado() throws Exception {
        when(service.create(any())).thenThrow(AcademicBusinessRuleException.duplicate("RN-01",
                "Ya existe un curso con el codigo 'prog-ii'"));

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("""
                        {"code":"prog-ii","name":"Duplicado"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.details[0]").value("rule: RN-01"))
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    @DisplayName("POST con descripcion de mas de 255 caracteres responde 400")
    void create_responde400PorDescripcionLarga() throws Exception {
        String longDescription = "x".repeat(256);

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(
                        "{\"code\":\"X1\",\"name\":\"X\",\"description\":\"" + longDescription + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value(startsWith("description")));
    }

    @Test
    @DisplayName("GET /code/{code} responde 200")
    void findByCode_responde200() throws Exception {
        when(service.findByCode("prog-ii")).thenReturn(response(CourseStatus.ACTIVE));

        mockMvc.perform(get(BASE + "/code/prog-ii"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROG-II"));
    }

    @Test
    @DisplayName("GET /{id} inexistente responde 404")
    void findById_responde404() throws Exception {
        when(service.findById(999L)).thenThrow(new AcademicResourceNotFoundException("No existe un curso con id 999"));

        mockMvc.perform(get(BASE + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("GET lista con estado no reconocido responde 400")
    void findAll_responde400PorEstadoDesconocido() throws Exception {
        when(service.findAll(eq("BORRADO"), any(Pageable.class)))
                .thenThrow(AcademicBusinessRuleException.invalidValue("Estado no reconocido: 'BORRADO'"));

        mockMvc.perform(get(BASE).param("status", "BORRADO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("GET lista paginada responde 200")
    void findAll_responde200() throws Exception {
        when(service.findAll(eq("ACTIVE"), any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(response(CourseStatus.ACTIVE)), 0, 20, 1, 1));

        mockMvc.perform(get(BASE).param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("PROG-II"))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("PUT con creditos en cero responde 400 (RN-08)")
    void update_responde400PorRN08() throws Exception {
        when(service.update(eq(7L), any())).thenThrow(AcademicBusinessRuleException.badRequest("RN-08",
                "Si se envian creditos, deben ser mayores que cero"));

        mockMvc.perform(put(BASE + "/7").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Programacion II","credits":0}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("rule: RN-08"));
    }

    @Test
    @DisplayName("PATCH /{id}/status desactiva el curso y expone INACTIVE (RN-09)")
    void changeStatus_responde200() throws Exception {
        when(service.changeStatus(eq(7L), any())).thenReturn(response(CourseStatus.INACTIVE));

        mockMvc.perform(patch(BASE + "/7/status").contentType(MediaType.APPLICATION_JSON).content("""
                        {"status":"INACTIVE"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("PATCH /{id}/status sin estado responde 400")
    void changeStatus_responde400SinEstado() throws Exception {
        mockMvc.perform(patch(BASE + "/7/status").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("RN-05: DELETE no esta expuesto y responde 405")
    void delete_noEstaPermitido() throws Exception {
        mockMvc.perform(delete(BASE + "/7")).andExpect(status().isMethodNotAllowed());
    }
}
