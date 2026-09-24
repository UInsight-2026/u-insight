package gt.edu.uinsight.teacher;

import com.fasterxml.jackson.databind.ObjectMapper;
import gt.edu.uinsight.section.repository.SectionRepository;
import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.model.TeacherStatus;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

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
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private SectionRepository sectionRepository;

    @BeforeEach
    void cleanDatabase() {
        sectionRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    private String json(Map<String, Object> body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private Long createTeacher(String code, String name, String email) throws Exception {
        String response = mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("teacherCode", code, "teacherName", name, "email", email))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private void createSection(String sectionCode, Long teacherId) throws Exception {
        mockMvc.perform(post("/api/v1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("sectionCode", sectionCode, "courseName", "Programacion I",
                                "academicTerm", "2026-1", "teacherId", teacherId))))
                .andExpect(status().isCreated());
    }

    // --- Regla: codigo unico ---

    @Test
    void createTeacherRejectsDuplicateCode() throws Exception {
        createTeacher("DOC-001", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("teacherCode", "doc-001", "teacherName", "Otro Docente"))))
                .andExpect(status().isConflict());
    }

    @Test
    void updateTeacherRejectsCodeOfAnotherTeacher() throws Exception {
        createTeacher("DOC-001", "Ana Lopez", "ana.lopez@uinsight.edu.gt");
        Long secondId = createTeacher("DOC-002", "Luis Perez", "luis.perez@uinsight.edu.gt");

        mockMvc.perform(put("/api/v1/teachers/" + secondId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("teacherCode", "DOC-001", "teacherName", "Luis Perez"))))
                .andExpect(status().isConflict());
    }

    // --- Regla: el correo debe ser valido si se utiliza ---

    @Test
    void createTeacherRejectsInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("teacherCode", "DOC-010", "teacherName", "Correo Malo",
                                "email", "correo-invalido"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTeacherAcceptsEmptyEmail() throws Exception {
        mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("teacherCode", "DOC-011", "teacherName", "Sin Correo",
                                "email", ""))))
                .andExpect(status().isCreated());
    }

    // --- PUT /api/v1/teachers/{id} ---

    @Test
    void updateTeacherChangesData() throws Exception {
        Long id = createTeacher("DOC-020", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(put("/api/v1/teachers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("teacherCode", "DOC-020", "teacherName", "Ana Maria Lopez",
                                "email", "ana.m.lopez@uinsight.edu.gt"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teacherName").value("Ana Maria Lopez"))
                .andExpect(jsonPath("$.email").value("ana.m.lopez@uinsight.edu.gt"));
    }

    @Test
    void updateTeacherReturnsNotFoundForUnknownId() throws Exception {
        mockMvc.perform(put("/api/v1/teachers/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("teacherCode", "DOC-999", "teacherName", "Fantasma"))))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /api/v1/teachers/{id}/status ---

    @Test
    void patchStatusChangesTeacherStatus() throws Exception {
        Long id = createTeacher("DOC-030", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(patch("/api/v1/teachers/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "INACTIVE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void patchStatusRejectsUnknownStatus() throws Exception {
        Long id = createTeacher("DOC-031", "Ana Lopez", "ana.lopez@uinsight.edu.gt");

        mockMvc.perform(patch("/api/v1/teachers/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "SUSPENDIDO"))))
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
        Long id = createTeacher("DOC-050", "Ana Lopez", "ana.lopez@uinsight.edu.gt");
        mockMvc.perform(patch("/api/v1/teachers/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "INACTIVE"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("sectionCode", "SEC-050", "courseName", "Programacion I",
                                "academicTerm", "2026-1", "teacherId", id))))
                .andExpect(status().isConflict());
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
