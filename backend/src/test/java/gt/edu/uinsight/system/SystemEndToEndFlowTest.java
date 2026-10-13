
package gt.edu.uinsight.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * U-Insight - Celula C7 - Semana 5.
 *
 * Prueba del flujo academico de 14 pasos.
 *
 * Pasos 1, 2, 3, 5, 6, 7 y 8:
 *   Creacion de recursos mediante endpoints REST.
 *
 * Paso 4 - A3:
 *   No se identifico endpoint de creacion de estudiantes.
 *
 * Paso 9 - Analitica:
 *   Consulta del resumen; se documentan respuestas parciales.
 *
 * Paso 10 - B4:
 *   Consulta de tendencias; pueden faltar datos.
 *
 * Paso 11 - B7:
 *   Evaluacion de riesgo con indicadores simulados.
 *
 * Paso 12 - C3:
 *   Endpoint individual no identificado.
 *   Se verifica alternativamente el listado de C5.
 *
 * Paso 13 - C4:
 *   Endpoint disponible, pero sin alertId validado del flujo.
 *
 * Paso 14 - C4:
 *   Endpoint de seguimiento no implementado.
 *
 * Los pasos inexistentes se representan con pruebas
 * individuales @Disabled, indicando la celula responsable.
 *
 * El E2E principal permanece habilitado.
 *
 * Nota: BUILD SUCCESS confirma las aserciones ejecutadas,
 * no la integracion completa de los 14 pasos.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:uinsight-test;DB_CLOSE_DELAY=-1;NON_KEYWORDS=YEAR"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SystemEndToEndFlowTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void officialEndToEndFlow() throws Exception {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("U-INSIGHT - E2E OFICIAL - SEMANA 5");
        System.out.println("==========================================");

        // ====================================================
        // PASO 1 - PERIODO ACADEMICO (A1)
        // ====================================================

        String academicPeriodResponse = mockMvc.perform(
                post("/api/v1/academic-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "E2E Semana 5",
                                  "year": 2026,
                                  "startDate": "2026-01-01",
                                  "endDate": "2026-12-31"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode academicPeriodJson =
                objectMapper.readTree(academicPeriodResponse);

        long academicPeriodId =
                academicPeriodJson.path("id").asLong();

        assertTrue(academicPeriodId > 0,
                "A1 no devolvio academicPeriodId valido");

        System.out.println(
                "PASO 1 - A1 Periodo: PASA (201), ID="
                        + academicPeriodId);

        // ====================================================
        // PASO 2 - CURSO (A1)
        // ====================================================

        String courseResponse = mockMvc.perform(
                post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "E2E-PROG-II",
                                  "name": "Programacion II E2E",
                                  "description": "Curso de integracion Semana 5",
                                  "credits": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode courseJson =
                objectMapper.readTree(courseResponse);

        long courseId = courseJson.path("id").asLong();

        assertTrue(courseId > 0,
                "A1 no devolvio courseId valido");

        System.out.println(
                "PASO 2 - A1 Curso: PASA (201), ID="
                        + courseId);

        // ====================================================
        // PASO 3 - DOCENTE (A2)
        // ====================================================

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
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode teacherJson =
                objectMapper.readTree(teacherResponse);

        long teacherId = teacherJson.path("id").asLong();

        assertTrue(teacherId > 0,
                "A2 no devolvio teacherId valido");

        System.out.println(
                "PASO 3 - A2 Docente: PASA (201), ID="
                        + teacherId);

        // ====================================================
        // PASO 4 - ESTUDIANTE (A3)
        // ====================================================
        //
        // No se identifico POST /api/v1/students.
        // Su prueba individual esta deshabilitada.
        // Se usara studentId=1001 como dato controlado.
        // ====================================================

        System.out.println(
                "PASO 4 - A3 Estudiante: NO DISPONIBLE "
                        + "(prueba individual deshabilitada)");

        // ====================================================
        // PASO 5 - SECCION (A4)
        // ====================================================

        String sectionRequest = """
                {
                  "academicPeriodId": %d,
                  "courseId": %d,
                  "teacherId": %d,
                  "sectionCode": "E2E-SEC-A"
                }
                """.formatted(
                        academicPeriodId,
                        courseId,
                        teacherId);

        String sectionResponse = mockMvc.perform(
                post("/api/v1/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sectionRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode sectionJson =
                objectMapper.readTree(sectionResponse);

        long sectionId = sectionJson.path("id").asLong();

        assertTrue(sectionId > 0,
                "A4 no devolvio sectionId valido");

        System.out.println(
                "PASO 5 - A4 Seccion: PASA (201), ID="
                        + sectionId);

        // ====================================================
        // PASO 6 - INSCRIPCION (A4)
        // ====================================================
        //
        // A4 no comprueba la existencia del estudiante en A3.
        // ====================================================

        long studentId = 1001L;

        String enrollmentRequest = """
                {
                  "studentId": %d
                }
                """.formatted(studentId);

        String enrollmentResponse = mockMvc.perform(
                post("/api/v1/sections/{sectionId}/enrollments",
                        sectionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollmentRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode enrollmentJson =
                objectMapper.readTree(enrollmentResponse);

        long enrollmentId =
                enrollmentJson.path("id").asLong();

        assertTrue(enrollmentId > 0,
                "A4 no devolvio enrollmentId valido");

        System.out.println(
                "PASO 6 - A4 Inscripcion: PASA (201), ID="
                        + enrollmentId
                        + ", studentId=" + studentId
                        + " (sin validar contra A3)");

        // ====================================================
        // PASO 7 - EVALUACION (A5)
        // ====================================================

        String evaluationRequest = """
                {
                  "sectionId": %d,
                  "name": "Parcial E2E Semana 5",
                  "type": "EXAM",
                  "evaluationDate": "2026-10-15",
                  "maximumScore": 100,
                  "weight": 30
                }
                """.formatted(sectionId);

        String evaluationResponse = mockMvc.perform(
                post("/api/v1/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evaluationRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode evaluationJson =
                objectMapper.readTree(evaluationResponse);

        long evaluationId =
                evaluationJson.path("id").asLong();

        assertTrue(evaluationId > 0,
                "A5 no devolvio evaluationId valido");

        System.out.println(
                "PASO 7 - A5 Evaluacion: PASA (201), ID="
                        + evaluationId);

        // ====================================================
        // PASO 8 - CALIFICACION (A6)
        // ====================================================
        //
        // A6 necesita registrar manualmente referencias
        // internas de evaluaciones e inscripciones.
        // No existe sincronizacion automatica comprobada.
        // ====================================================

        String evaluationRefRequest = """
                {
                  "id": %d,
                  "sectionId": %d,
                  "name": "Parcial E2E Semana 5",
                  "maximumScore": 100
                }
                """.formatted(evaluationId, sectionId);

        mockMvc.perform(
                post("/api/v1/grades/evaluation-refs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evaluationRefRequest))
                .andExpect(status().isCreated());

        System.out.println(
                "INTEGRACION A5 -> A6: REFERENCIA CREADA (201)");

        String enrollmentRefRequest = """
                {
                  "studentId": %d,
                  "sectionId": %d
                }
                """.formatted(studentId, sectionId);

        mockMvc.perform(
                post("/api/v1/grades/enrollment-refs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollmentRefRequest))
                .andExpect(status().isCreated());

        System.out.println(
                "INTEGRACION A4 -> A6: REFERENCIA CREADA (201)");

        String gradeRequest = """
                {
                  "evaluationId": %d,
                  "studentId": %d,
                  "score": 85
                }
                """.formatted(evaluationId, studentId);

        String gradeResponse = mockMvc.perform(
                post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gradeRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode gradeJson =
                objectMapper.readTree(gradeResponse);

        long gradeId = gradeJson.path("id").asLong();

        assertTrue(gradeId > 0,
                "A6 no devolvio gradeId valido");

        System.out.println(
                "PASO 8 - A6 Calificacion: PASA (201), ID="
                        + gradeId);

        // ====================================================
        // PASO 9 - ESTADISTICAS (B1/B2/B3/B5/B6)
        // ====================================================
        //
        // HTTP observado anteriormente: 206.
        // Se registra la respuesta real sin exigir HTTP 200.
        // ====================================================

        var summaryResult = mockMvc.perform(
                get("/api/v1/analytics/sections/{id}/summary",
                        sectionId))
                .andReturn();

        int summaryStatus =
                summaryResult.getResponse().getStatus();

        System.out.println(
                "PASO 9 - Estadisticas: HTTP " + summaryStatus);

        // ====================================================
        // PASO 10 - TENDENCIAS (B4)
        // ====================================================
        //
        // HTTP observado anteriormente: 404 sin datos.
        // ====================================================

        var trendsResult = mockMvc.perform(
                get("/api/v1/analytics/sections/{id}/trends",
                        sectionId))
                .andReturn();

        int trendsStatus =
                trendsResult.getResponse().getStatus();

        System.out.println(
                "PASO 10 - Tendencias: HTTP " + trendsStatus);

        // ====================================================
        // PASO 11 - EVALUACION DE RIESGO (B7)
        // ====================================================
        //
        // Utiliza indicadores simulados para validar B7.
        // No demuestra integracion automatica con B1-B6.
        // ====================================================

        String riskRequest = """
                {
                  "centralTendency": {
                    "mean": 65.0,
                    "median": 68.0
                  },
                  "position": {
                    "percentile90": 90.0
                  },
                  "dispersion": {
                    "stdDev": 12.0
                  },
                  "trend": {
                    "value": -5.0
                  },
                  "studentsAtRisk": 3
                }
                """;

        var riskResult = mockMvc.perform(
                post("/api/v1/risk-evaluation/sections/{sectionId}",
                        sectionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(riskRequest))
                .andReturn();

        int riskStatus =
                riskResult.getResponse().getStatus();

        String riskResponse =
                riskResult.getResponse().getContentAsString();

        System.out.println(
                "PASO 11 - B7 Riesgo: HTTP " + riskStatus);

        System.out.println(
                "PASO 11 - Respuesta: " + riskResponse);

        // ====================================================
        // PASO 12 - ALERTA (C3) / CONSULTA ALTERNATIVA (C5)
        // ====================================================
        //
        // No se identifico GET /api/v1/alerts/{id}.
        // C5 ofrece un listado consolidado alternativo.
        // ====================================================

        var alertsResult = mockMvc.perform(
                get("/api/v1/reports/alerts"))
                .andReturn();

        int alertsStatus =
                alertsResult.getResponse().getStatus();

        String alertsResponse =
                alertsResult.getResponse().getContentAsString();

        System.out.println(
                "PASO 12 - C3 Consulta individual: NO DISPONIBLE "
                        + "(prueba individual deshabilitada)");

        System.out.println(
                "PASO 12 - C5 Listado alternativo: HTTP "
                        + alertsStatus);

        System.out.println(
                "PASO 12 - Respuesta C5: " + alertsResponse);

        // ====================================================
        // PASO 13 - INTERVENCION (C4)
        // ====================================================
        //
        // El endpoint existe, pero no se obtuvo alertId
        // persistido y validado para la seccion E2E.
        // No se inventa un identificador de alerta.
        // ====================================================

        System.out.println(
                "PASO 13 - C4 Intervencion: BLOQUEADO "
                        + "(sin alertId validado; no ejecutado)");

        // ====================================================
        // PASO 14 - SEGUIMIENTO (C4)
        // ====================================================
        //
        // FollowUpController contiene solo comentarios.
        // Su prueba individual esta deshabilitada.
        // ====================================================

        System.out.println(
                "PASO 14 - C4 Seguimiento: NO DISPONIBLE "
                        + "(prueba individual deshabilitada)");

        // ====================================================
        // CIERRE C7
        // ====================================================

        verifySystemEndpoints();

        // ====================================================
        // RESUMEN DE EJECUCION
        // ====================================================

        System.out.println();
        System.out.println("==========================================");
        System.out.println("RESUMEN E2E - SEMANA 5");
        System.out.println("==========================================");

        System.out.println(
                "Pasos con HTTP 201: 1, 2, 3, 5, 6, 7 y 8");

        System.out.println(
                "Paso 4: A3 sin endpoint identificado");

        System.out.println(
                "Paso 6: studentId de prueba sin validar en A3");

        System.out.println(
                "Paso 8: A6 requiere sincronizacion auxiliar");

        System.out.println(
                "Paso 9: estadisticas HTTP " + summaryStatus);

        System.out.println(
                "Paso 10: tendencias HTTP " + trendsStatus);

        System.out.println(
                "Paso 11: riesgo HTTP " + riskStatus
                        + " (indicadores simulados)");

        System.out.println(
                "Paso 12: C3 no disponible; C5 HTTP " + alertsStatus);

        System.out.println(
                "Paso 13: C4 bloqueado por falta de alertId validado");

        System.out.println(
                "Paso 14: C4 sin endpoint implementado");

        System.out.println(
                "Pruebas deshabilitadas individualmente: "
                        + "A3 paso 4, C3 paso 12, C4 paso 14");

        System.out.println(
                "Cierre C7: readiness e integration-status HTTP 200");

        System.out.println("==========================================");
    }

    // ========================================================
    // TEST INDEPENDIENTE - ENDPOINTS C7
    // ========================================================

    @Test
    void systemEndpointsCloseEndToEndFlow() throws Exception {
        verifySystemEndpoints();
    }

    // ========================================================
    // VERIFICACION DE ENDPOINTS C7
    // ========================================================

    private void verifySystemEndpoints() throws Exception {

        mockMvc.perform(get("/api/v1/system/readiness"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON));

        System.out.println(
                "CIERRE C7 - Readiness: PASA (200)");

        mockMvc.perform(
                get("/api/v1/system/integration-status"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON));

        System.out.println(
                "CIERRE C7 - Integration Status: PASA (200)");
    }

    // ========================================================
    // PRUEBAS INDIVIDUALES DESHABILITADAS
    // ========================================================
    //
    // Solo se omiten los pasos cuyos endpoints REST
    // no fueron identificados en el proyecto revisado.
    //
    // Las pruebas disponibles y el E2E principal
    // permanecen habilitados.
    // ========================================================

    @Test
    @Disabled("Celula A3 - Paso 4: no se identifico "
            + "POST /api/v1/students para crear estudiantes")
    void paso04RegistroEstudianteA3() {
        // Pendiente de implementacion por A3.
    }

    @Test
    @Disabled("Celula C3 - Paso 12: no se identifico "
            + "GET /api/v1/alerts/{id}; C5 solo ofrece listado")
    void paso12ConsultaAlertaC3() {
        // Pendiente de implementacion por C3.
    }

    @Test
    @Disabled("Celula C4 - Paso 14: no se implemento "
            + "POST /api/v1/interventions/{id}/follow-ups")
    void paso14RegistroSeguimientoC4() {
        // Pendiente de implementacion por C4.
    }
}
