package gt.edu.uinsight.analytics.trend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TrendEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        insertStudent(501, "S-501");
        insertStudent(502, "S-502");

        jdbcTemplate.update("""
                INSERT INTO section
                    (id, academic_period_id, course_id, teacher_id, section_code, status)
                VALUES (10, 1, 1, 1, 'A', 'ACTIVE')
                """);

        jdbcTemplate.update("""
                INSERT INTO evaluation
                    (id, section_id, name, type, evaluation_date, maximum_score, weight, status)
                VALUES (101, 10, 'E1', 'EXAM', DATE '2026-09-01', 100.00, 50.00, 'CLOSED')
                """);
        jdbcTemplate.update("""
                INSERT INTO evaluation
                    (id, section_id, name, type, evaluation_date, maximum_score, weight, status)
                VALUES (102, 10, 'E2', 'EXAM', DATE '2026-09-15', 50.00, 50.00, 'CLOSED')
                """);

        insertGrade(1001, 101, 501, "80.00");
        insertGrade(1002, 101, 502, "60.00");
        insertGrade(1003, 102, 501, "45.00");
        insertGrade(1004, 102, 502, "35.00");
    }

    @Test
    void sectionTrendReadsDatabaseAndReturnsOrderedAverages() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/sections/10/trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classification").value("POSITIVE"))
                .andExpect(jsonPath("$.averageChange").value(10.0))
                .andExpect(jsonPath("$.points[0].label").value("E1"))
                .andExpect(jsonPath("$.points[0].value").value(70))
                .andExpect(jsonPath("$.points[1].label").value("E2"))
                .andExpect(jsonPath("$.points[1].value").value(80));
    }

    @Test
    void studentTrendReadsDatabaseAndReturnsOrderedScores() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/students/501/trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classification").value("POSITIVE"))
                .andExpect(jsonPath("$.averageChange").value(10.0))
                .andExpect(jsonPath("$.points[0].value").value(80))
                .andExpect(jsonPath("$.points[1].value").value(90));
    }

    @Test
    void missingSectionReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/sections/999/trend"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void missingStudentReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/students/999/trend"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    private void insertStudent(long id, String studentCode) {
        jdbcTemplate.update("""
                INSERT INTO student (id, student_code, status)
                VALUES (?, ?, 'ACTIVE')
                """, id, studentCode);
    }

    private void insertGrade(long id, long evaluationId, long studentId, String score) {
        jdbcTemplate.update("""
                INSERT INTO grade
                    (id, evaluation_id, student_id, score, registered_at, updated_at)
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """, id, evaluationId, studentId, new java.math.BigDecimal(score));
    }
}
