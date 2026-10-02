package gt.edu.uinsight.teacher;

import com.jayway.jsonpath.JsonPath;
import gt.edu.uinsight.teacher.repository.TeacherSectionRepository;
import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.model.TeacherStatus;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Pruebas de los endpoints de docentes y de las reglas de negocio del modulo. */
@SpringBootTest
@AutoConfigureMockMvc
class TeacherEndpointsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private TeacherSectionRepository sectionRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    /** Ids propios de esta prueba, fuera del rango que usan las pruebas de otras celulas. */
    private static final AtomicLong SECTION_IDS = new AtomicLong(9000);

    @BeforeEach
    void cleanDatabase() {
        sectionRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    private String teacherJson(String code, String name, String email) {
        return """
                {"teacherCode": "%s", "teacherName": "%s", "email": "%s"}
                """.formatted(code, name, email);
    }

    private Long createTeacher(String code, String name, String email) throws Exception {
        String response = mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson(code, name, email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    /**
     * La creacion de secciones pertenece a la celula B4 y su entidad es de solo lectura,
     * asi que la fila se inserta directamente en la tabla compartida.
     */
    private Long createSection(String sectionCode, Long teacherId) {
        Long sectionId = SECTION_IDS.incrementAndGet();
        jdbcTemplate.update("""
                INSERT INTO section
                    (id, academic_period_id, course_id, teacher_id, section_code, status)
                VALUES (?, 1, 1, ?, ?, 'ACTIVE')
                """, sectionId, teacherId, sectionCode);
        return sectionId;
    }

    // --- Regla: codigo unico ---

    @Test
    void createTeacherRejectsDuplicateCode() throws Exception {
        createTeacher("DOC-001", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson("doc-001", "Otro Docente", "otro@uinsight.edu.gt")))
                .andExpect(status().isConflict());
    }

    @Test
    void updateTeacherRejectsCodeOfAnotherTeacher() throws Exception {
        createTeacher("DOC-001", "Ana Lopez", "ana.lopez@uinsight.edu.gt");
        Long secondId = createTeacher("DOC-002", "Luis Perez", "luis.perez@uinsight.edu.gt");

        mockMvc.perform(put("/api/v1/teachers/" + secondId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson("DOC-001", "Luis Perez", "luis.perez@uinsight.edu.gt")))
                .andExpect(status().isConflict());
    }

    // --- Regla: el correo debe ser valido si se utiliza ---

    @Test
    void createTeacherRejectsInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson("DOC-010", "Correo Malo", "correo-invalido")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTeacherAcceptsEmptyEmail() throws Exception {
        mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson("DOC-011", "Sin Correo", "")))
                .andExpect(status().isCreated());
    }

    // --- PUT /api/v1/teachers/{id} ---

    @Test
    void updateTeacherChangesData() throws Exception {
        Long id = createTeacher("DOC-020", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(put("/api/v1/teachers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson("DOC-020", "Ana Maria Lopez", "ana.m.lopez@uinsight.edu.gt")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teacherName").value("Ana Maria Lopez"))
                .andExpect(jsonPath("$.email").value("ana.m.lopez@uinsight.edu.gt"));
    }

    @Test
    void updateTeacherReturnsNotFoundForUnknownId() throws Exception {
        mockMvc.perform(put("/api/v1/teachers/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson("DOC-999", "Fantasma", "fantasma@uinsight.edu.gt")))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /api/v1/teachers/{id}/status ---

    @Test
    void patchStatusChangesTeacherStatus() throws Exception {
        Long id = createTeacher("DOC-030", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(patch("/api/v1/teachers/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void patchStatusRejectsUnknownStatus() throws Exception {
        Long id = createTeacher("DOC-031", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(patch("/api/v1/teachers/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"SUSPENDIDO\"}"))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/v1/teachers/{id}/sections ---

    @Test
    void getSectionsReturnsTeacherSections() throws Exception {
        Long id = createTeacher("DOC-040", "Ana Lopez", "ana.lopez@uinsight.edu.gt");
        createSection("SEC-040", id);

        mockMvc.perform(get("/api/v1/teachers/" + id + "/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sectionCode").value("SEC-040"))
                .andExpect(jsonPath("$[0].teacherId").value(id));
    }

    @Test
    void getSectionsReturnsNotFoundForUnknownTeacher() throws Exception {
        mockMvc.perform(get("/api/v1/teachers/9999/sections"))
                .andExpect(status().isNotFound());
    }

    // --- Regla: no asignar docentes inactivos a nuevas secciones ---

    @Test
    void inactiveTeacherCannotBeAssignedToNewSection() throws Exception {
        Long previousId = createTeacher("DOC-050A", "Docente Saliente", "saliente@uinsight.edu.gt");
        Long id = createTeacher("DOC-050", "Ana Lopez", "ana.lopez@uinsight.edu.gt");
        Long sectionId = createSection("SEC-050", previousId);

        mockMvc.perform(patch("/api/v1/teachers/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"INACTIVE\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/teachers/" + id + "/sections/" + sectionId))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/teachers/" + previousId + "/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void activeTeacherCanBeAssignedToSection() throws Exception {
        Long previousId = createTeacher("DOC-051A", "Docente Saliente", "saliente2@uinsight.edu.gt");
        Long id = createTeacher("DOC-051", "Ana Lopez", "ana.lopez@uinsight.edu.gt");
        Long sectionId = createSection("SEC-051", previousId);

        mockMvc.perform(put("/api/v1/teachers/" + id + "/sections/" + sectionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teacherId").value(id));

        mockMvc.perform(get("/api/v1/teachers/" + previousId + "/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void assignSectionReturnsNotFoundForUnknownSection() throws Exception {
        Long id = createTeacher("DOC-052", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(put("/api/v1/teachers/" + id + "/sections/999999"))
                .andExpect(status().isNotFound());
    }

    // --- Regla: no eliminar fisicamente docentes con historial ---

    @Test
    void teacherWithSectionsCannotBeDeleted() throws Exception {
        Long id = createTeacher("DOC-060", "Ana Lopez", "ana.lopez@uinsight.edu.gt");
        createSection("SEC-060", id);

        mockMvc.perform(delete("/api/v1/teachers/" + id))
                .andExpect(status().isConflict());

        Teacher stored = teacherRepository.findById(id).orElseThrow();
        Assertions.assertEquals(TeacherStatus.ACTIVE, stored.getStatus());
    }

    @Test
    void teacherWithoutHistoryCanBeDeleted() throws Exception {
        Long id = createTeacher("DOC-070", "Sin Historial", "sin.historial@uinsight.edu.gt");

        mockMvc.perform(delete("/api/v1/teachers/" + id))
                .andExpect(status().isNoContent());
    }
}
