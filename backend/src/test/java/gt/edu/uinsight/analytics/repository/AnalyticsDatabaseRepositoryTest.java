package gt.edu.uinsight.analytics.repository;

import gt.edu.uinsight.analytics.centraltendency.exception.InvalidAnalyticsRequestException;
import gt.edu.uinsight.analytics.centraltendency.repository.GradeDataRepositoryImpl;
import gt.edu.uinsight.analytics.dispersion.repository.DispersionGradeRepository;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalyticsDatabaseRepositoryTest {

    private AnalyticsDatabaseRepository repository;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:analytics;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DROP ALL OBJECTS");
        jdbcTemplate.execute("CREATE TABLE student (id BIGINT PRIMARY KEY, student_code VARCHAR(30))");
        jdbcTemplate.execute("CREATE TABLE course (id BIGINT PRIMARY KEY)");
        jdbcTemplate.execute("CREATE TABLE academic_period (id BIGINT PRIMARY KEY)");
        jdbcTemplate.execute("""
                CREATE TABLE section (
                    id BIGINT PRIMARY KEY, course_id BIGINT, academic_period_id BIGINT, status VARCHAR(20))
                """);
        jdbcTemplate.execute("""
                CREATE TABLE evaluation (
                    id BIGINT PRIMARY KEY, section_id BIGINT, evaluation_date DATE, maximum_score DECIMAL(8,2))
                """);
        jdbcTemplate.execute("""
                CREATE TABLE grade (
                    id BIGINT PRIMARY KEY, evaluation_id BIGINT, student_id BIGINT, score DECIMAL(8,2))
                """);
        jdbcTemplate.execute("""
                CREATE TABLE enrollment (
                    id BIGINT PRIMARY KEY, student_id BIGINT, section_id BIGINT, status VARCHAR(20))
                """);

        jdbcTemplate.update("INSERT INTO student VALUES (1, 'EST2024001')");
        jdbcTemplate.update("INSERT INTO student VALUES (2, 'EST2024002')");
        jdbcTemplate.update("INSERT INTO student VALUES (3, 'EST2024003')");
        jdbcTemplate.update("INSERT INTO student VALUES (4, 'EST2024004')");
        jdbcTemplate.update("INSERT INTO course VALUES (10)");
        jdbcTemplate.update("INSERT INTO academic_period VALUES (20)");
        jdbcTemplate.update("INSERT INTO section VALUES (100, 10, 20, 'ACTIVE')");
        jdbcTemplate.update("INSERT INTO section VALUES (101, 10, 20, 'CLOSED')");
        jdbcTemplate.update("INSERT INTO evaluation VALUES (1000, 100, DATE '2026-01-10', 100)");
        jdbcTemplate.update("INSERT INTO evaluation VALUES (1001, 101, DATE '2026-01-11', 50)");
        jdbcTemplate.update("INSERT INTO grade VALUES (1, 1000, 1, 80)");
        jdbcTemplate.update("INSERT INTO grade VALUES (2, 1001, 1, 40)");
        jdbcTemplate.update("INSERT INTO grade VALUES (3, 1000, 2, 59)");
        jdbcTemplate.update("INSERT INTO grade VALUES (4, 1000, 3, 60)");
        jdbcTemplate.update("INSERT INTO grade VALUES (5, 1000, 4, 20)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES (1, 1, 100, 'ACTIVE')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES (2, 1, 101, 'INACTIVE')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES (3, 2, 100, 'ACTIVE')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES (4, 3, 100, 'ACTIVE')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES (5, 4, 100, 'INACTIVE')");
        repository = new AnalyticsDatabaseRepository(jdbcTemplate);
    }

    @Test
    void readsCanonicalSingularTablesForAnalyticsModules() {
        assertEquals("EST2024001", repository.findStudentCode(1L));
        assertTrue(repository.studentExists(1L));
        assertEquals(List.of(100L), repository.findActiveSectionIdsByStudent(1L));
        assertEquals(List.of(80.0, 59.0, 60.0, 20.0), repository.findGradesBySection(100L).stream()
                .map(grade -> grade.score().doubleValue()).toList());
        assertEquals(List.of(80.0, 59.0, 60.0, 20.0), repository.findGradesByCourse(10L, 20L).stream()
                .map(grade -> grade.score().doubleValue()).toList());
    }

    @Test
    void mapsDatabaseRowsForCentralTendencyAndDispersion() {
        GradeDataRepositoryImpl centralTendencyRepository = new GradeDataRepositoryImpl(repository);
        DispersionGradeRepository dispersionRepository = new DispersionGradeRepository(repository);

        assertEquals(new BigDecimal("80.00"), centralTendencyRepository.findBySectionId(100L, null)
                .getFirst().getScore());
        assertEquals(List.of(new BigDecimal("80.00"), new BigDecimal("59.00"),
                new BigDecimal("60.00"), new BigDecimal("20.00")),
                dispersionRepository.findScoresBySectionId(100L));
        assertTrue(dispersionRepository.courseExists(10L));
        assertFalse(dispersionRepository.courseExists(999L));
    }

    @Test
    void validatesEvaluationBelongsToRequestedSection() {
        GradeDataRepositoryImpl centralTendencyRepository = new GradeDataRepositoryImpl(repository);

        assertThrows(InvalidAnalyticsRequestException.class,
                () -> centralTendencyRepository.findBySectionId(100L, 1001L));
    }

    @Test
    void countsOnlyActivelyEnrolledStudentsWhoseNormalizedAverageIsBelowThreshold() {
        assertEquals(1, repository.countStudentsBelowAverage(100L, 60));
        assertEquals(2, repository.countStudentsBelowAverage(100L, 61));
    }
}
