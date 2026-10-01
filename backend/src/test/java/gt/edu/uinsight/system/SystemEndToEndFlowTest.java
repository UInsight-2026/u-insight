package gt.edu.uinsight.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SystemEndToEndFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Disabled(
        "Bloqueado por error de compilación externo a C7 en "
        + "AcademicExceptionHandler.java: PropertyReferenceException "
        + "no encontrada en org.springframework.data.mapping."
    )
    @Test
    void officialEndToEndFlow() throws Exception {

        /*
         * ============================================================
         * PASO 1 - PERÍODO
         * Célula: A1
         * ============================================================
         *
         * POST /api/v1/academic-periods
         */

        String academicPeriodResponse = mockMvc.perform(
                post("/api/v1/academic-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "E2E Semana 4",
                              "year": 2026,
                              "startDate": "2026-01-01",
                              "endDate": "2026-12-31"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode academicPeriodJson =
                objectMapper.readTree(academicPeriodResponse);

        Long academicPeriodId = academicPeriodJson.get("id").asLong();

        /*
         * ============================================================
         * PASO 2 - CURSO
         * Célula: A1
         * ============================================================
         *
         * Endpoint oficial:
         * POST /api/v1/courses
         *
         * No se ejecuta porque el endpoint no fue identificado
         * en el código actual de develop.
         */

        /*
         * ============================================================
         * PASO 3 - DOCENTE
         * Célula: A2
         * ============================================================
         *
         * POST /api/v1/teachers
         */

        String teacherResponse = mockMvc.perform(
                post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "teacherCode": "E2E-T001",
                              "teacherName": "Docente E2E",
                              "email": "e2e.teacher@uinsight.test",
                              "status": "ACTIVE"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode teacherJson =
                objectMapper.readTree(teacherResponse);

        Long teacherId = teacherJson.get("id").asLong();

        /*
         * ============================================================
         * PASO 4 - ESTUDIANTES
         * Célula: A3
         * ============================================================
         *
         * Endpoint oficial:
         * POST /api/v1/students
         *
         * No se ejecuta porque el endpoint no fue identificado
         * en el código actual de develop.
         */

        /*
         * ============================================================
         * PASO 5 - SECCIÓN
         * Célula: A4
         * ============================================================
         *
         * POST /api/v1/sections
         *
         * Requiere:
         * academicPeriodId
         * courseId
         * teacherId
         * sectionCode
         *
         * No se ejecuta porque el paso 2 no proporciona courseId.
         */

        /*
         * ============================================================
         * PASO 6 - INSCRIPCIÓN
         * Célula: A4
         * ============================================================
         *
         * POST /api/v1/sections/{id}/enrollments
         *
         * Requiere sectionId y studentId.
         *
         * No se ejecuta porque los pasos anteriores no proporcionan
         * los identificadores necesarios.
         */

        /*
         * ============================================================
         * PASO 7 - EVALUACIÓN
         * Célula: A5
         * ============================================================
         *
         * POST /api/v1/evaluations
         *
         * Requiere sectionId.
         */

        /*
         * ============================================================
         * PASO 8 - CALIFICACIONES
         * Célula: A6
         * ============================================================
         *
         * POST /api/v1/grades
         *
         * Requiere evaluationId y studentId.
         */

        /*
         * ============================================================
         * PASO 9 - ESTADÍSTICAS
         * Células: B1, B2, B3, B5 y B6
         * ============================================================
         *
         * Endpoint de consolidación:
         * GET /api/v1/analytics/sections/{id}/summary
         *
         * No se ejecuta porque requiere datos académicos y
         * calificaciones generadas por los pasos anteriores.
         */

        /*
         * ============================================================
         * PASO 10 - TENDENCIA
         * Célula: B4
         * ============================================================
         *
         * GET /api/v1/analytics/sections/{id}/trend
         *
         * No se ejecuta porque requiere datos académicos.
         */

        /*
         * ============================================================
         * PASO 11 - RIESGO
         * Célula: B7
         * ============================================================
         *
         * Endpoint actualmente identificado en develop:
         * POST /api/v1/alert/b7/evaluar
         *
         * No se ejecuta porque requiere los resultados del análisis
         * y tendencia del flujo anterior.
         */

        /*
         * ============================================================
         * PASO 12 - ALERTA
         * Célula: C3
         * ============================================================
         *
         * Endpoints:
         * GET /api/v1/alerts
         * GET /api/v1/alerts/{id}
         * GET /api/v1/alerts/active
         * PATCH /api/v1/alerts/{id}/status
         *
         * La alerta es generada por B7 y administrada por C3.
         */

        /*
         * ============================================================
         * PASO 13 - INTERVENCIÓN
         * Célula: C4
         * ============================================================
         *
         * POST /api/v1/alerts/{id}/interventions
         *
         * Requiere alertId.
         */

        /*
         * ============================================================
         * PASO 14 - SEGUIMIENTO
         * Célula: C4
         * ============================================================
         *
         * POST /api/v1/interventions/{id}/follow-ups
         * GET /api/v1/interventions/{id}/follow-ups
         *
         * Requiere interventionId.
         */

        /*
         * ============================================================
         * CIERRE C7
         * ============================================================
         *
         * GET /api/v1/system/readiness
         * GET /api/v1/system/integration-status
         */

        mockMvc.perform(get("/api/v1/system/readiness"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/v1/system/integration-status"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Disabled(
        "A1 - Curso: el documento oficial define "
        + "POST /api/v1/courses, pero el endpoint no fue identificado "
        + "en el código actual de develop."
    )
    @Test
    void courseEndpointNotAvailable() {
    }

    @Disabled(
        "A3 - Estudiantes: el documento oficial define "
        + "POST /api/v1/students, pero el endpoint no fue identificado "
        + "en el código actual de develop."
    )
    @Test
    void studentEndpointNotAvailable() {
    }

    @Test
    void officialFlowCoverageDocumentation() {

        String[] coverage = {
            "01 | A1 | POST /api/v1/academic-periods | BLOQUEADO | Error de compilación en AcademicExceptionHandler",
            "02 | A1 | POST /api/v1/courses | NO DISPONIBLE EN DEVELOP | Endpoint oficial no identificado",
            "03 | A2 | POST /api/v1/teachers | BLOQUEADO | Error de compilación en AcademicExceptionHandler",
            "04 | A3 | POST /api/v1/students | NO DISPONIBLE EN DEVELOP | Endpoint oficial no identificado",
            "05 | A4 | POST /api/v1/sections | NO EJECUTADO | Requiere courseId",
            "06 | A4 | POST /api/v1/sections/{id}/enrollments | NO EJECUTADO | Requiere sectionId y studentId",
            "07 | A5 | POST /api/v1/evaluations | NO EJECUTADO | Requiere sectionId",
            "08 | A6 | POST /api/v1/grades | NO EJECUTADO | Requiere evaluationId y studentId",
            "09 | B1/B2/B3/B5/B6 | GET /api/v1/analytics/sections/{id}/summary | NO EJECUTADO | Requiere datos académicos",
            "10 | B4 | GET /api/v1/analytics/sections/{id}/trend | NO EJECUTADO | Requiere datos académicos",
            "11 | B7 | POST /api/v1/alert/b7/evaluar | NO EJECUTADO | Requiere datos analíticos",
            "12 | C3 | GET /api/v1/alerts/{id} | NO EJECUTADO | Requiere alerta generada por B7",
            "13 | C4 | POST /api/v1/alerts/{id}/interventions | NO EJECUTADO | Requiere alertId",
            "14 | C4 | POST /api/v1/interventions/{id}/follow-ups | NO EJECUTADO | Requiere interventionId",
            "C7 | C7 | GET /api/v1/system/readiness | BLOQUEADO | Error de compilación",
            "C7 | C7 | GET /api/v1/system/integration-status | BLOQUEADO | Error de compilación"
        };

        for (String row : coverage) {
            System.out.println(row);
        }
    }

    @Disabled(
        "Bloqueado por error de compilación externo a C7 en "
        + "AcademicExceptionHandler.java: PropertyReferenceException "
        + "no encontrada en org.springframework.data.mapping."
    )
    @Test
    void systemEndpointsCloseEndToEndFlow() throws Exception {

        mockMvc.perform(get("/api/v1/system/readiness"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/v1/system/integration-status"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}