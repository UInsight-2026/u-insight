package gt.edu.uinsight.analytics.repository;

import gt.edu.uinsight.analytics.centraltendency.model.GradeData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class AnalyticsDatabaseRepository {

    private static final RowMapper<AnalyticsGrade> GRADE_MAPPER = (resultSet, rowNumber) ->
            new AnalyticsGrade(
                    resultSet.getLong("grade_id"),
                    resultSet.getLong("evaluation_id"),
                    resultSet.getLong("student_id"),
                    resultSet.getLong("section_id"),
                    resultSet.getLong("course_id"),
                    resultSet.getBigDecimal("score"),
                    resultSet.getBigDecimal("maximum_score"),
                    resultSet.getDate("evaluation_date") == null
                            ? null
                            : resultSet.getDate("evaluation_date").toLocalDate());

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsDatabaseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean sectionExists(Long sectionId) {
        return exists("SELECT COUNT(*) FROM section WHERE id = ?", sectionId);
    }

    public boolean courseExists(Long courseId) {
        return exists("SELECT COUNT(*) FROM course WHERE id = ?", courseId);
    }

    public boolean periodExists(Long periodId) {
        return exists("SELECT COUNT(*) FROM academic_period WHERE id = ?", periodId);
    }

    public boolean evaluationBelongsToSection(Long evaluationId, Long sectionId) {
        return exists("SELECT COUNT(*) FROM evaluation WHERE id = ? AND section_id = ?",
                evaluationId, sectionId);
    }

    public boolean studentExists(Long studentId) {
        return exists("SELECT COUNT(*) FROM student WHERE id = ?", studentId);
    }

    public String findStudentCode(Long studentId) {
        return jdbcTemplate.query(
                "SELECT student_code FROM student WHERE id = ?",
                resultSet -> resultSet.next() ? resultSet.getString("student_code") : null,
                studentId);
    }

    public List<AnalyticsGrade> findGradesBySection(Long sectionId) {
        return jdbcTemplate.query(SECTION_GRADES + " ORDER BY e.evaluation_date, g.id",
                GRADE_MAPPER, sectionId);
    }

    public List<AnalyticsGrade> findGradesBySectionAndEvaluation(Long sectionId, Long evaluationId) {
        return jdbcTemplate.query(SECTION_GRADES + " AND e.id = ? ORDER BY e.evaluation_date, g.id",
                GRADE_MAPPER, sectionId, evaluationId);
    }

    public List<AnalyticsGrade> findGradesByCourse(Long courseId, Long periodId) {
        String sql = """
                SELECT g.id AS grade_id, g.evaluation_id, g.student_id,
                       e.section_id, s.course_id, g.score, e.maximum_score, e.evaluation_date
                FROM grade g
                JOIN evaluation e ON e.id = g.evaluation_id
                JOIN section s ON s.id = e.section_id
                WHERE s.course_id = ?
                  AND UPPER(s.status) = 'ACTIVE'
                """ + (periodId == null ? "" : " AND s.academic_period_id = ?")
                + " ORDER BY e.evaluation_date, g.id";
        return periodId == null
                ? jdbcTemplate.query(sql, GRADE_MAPPER, courseId)
                : jdbcTemplate.query(sql, GRADE_MAPPER, courseId, periodId);
    }

    public List<AnalyticsGrade> findGradesByStudent(Long studentId) {
        return jdbcTemplate.query(STUDENT_GRADES + " ORDER BY e.evaluation_date, g.id",
                GRADE_MAPPER, studentId);
    }

    public List<AnalyticsGrade> findGradesByStudentAndSection(Long studentId, Long sectionId) {
        return jdbcTemplate.query(STUDENT_GRADES + " AND e.section_id = ? ORDER BY e.evaluation_date, g.id",
                GRADE_MAPPER, studentId, sectionId);
    }

    public List<Long> findActiveSectionIdsByStudent(Long studentId) {
        return jdbcTemplate.queryForList("""
                SELECT DISTINCT section_id
                FROM enrollment
                WHERE student_id = ? AND UPPER(status) = 'ACTIVE'
                ORDER BY section_id
                """, Long.class, studentId);
    }

    public BigDecimal averageScoreForStudent(Long studentId, Long sectionId) {
        return average(findGradesByStudentAndSection(studentId, sectionId));
    }

    public BigDecimal averageScoreForSection(Long sectionId) {
        return average(findGradesBySection(sectionId));
    }

    public int countStudentsBelowAverage(Long sectionId, double threshold) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM (
                    SELECT enrollment.student_id
                    FROM enrollment
                    JOIN grade ON grade.student_id = enrollment.student_id
                    JOIN evaluation ON evaluation.id = grade.evaluation_id
                                  AND evaluation.section_id = enrollment.section_id
                    WHERE enrollment.section_id = ?
                      AND UPPER(enrollment.status) = 'ACTIVE'
                      AND evaluation.maximum_score > 0
                    GROUP BY enrollment.student_id
                    HAVING AVG(grade.score * 100.0 / evaluation.maximum_score) < ?
                ) students_below_average
                """, Integer.class, sectionId, threshold);
        return count == null ? 0 : count;
    }

    private BigDecimal average(List<AnalyticsGrade> grades) {
        if (grades.isEmpty()) {
            return null;
        }
        BigDecimal total = grades.stream()
                .map(AnalyticsGrade::score)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(grades.size()), 1, java.math.RoundingMode.HALF_UP);
    }

    private boolean exists(String sql, Long... ids) {
        return jdbcTemplate.queryForObject(sql, Integer.class, (Object[]) ids) > 0;
    }

    private static final String SECTION_GRADES = """
            SELECT g.id AS grade_id, g.evaluation_id, g.student_id,
                   e.section_id, s.course_id, g.score, e.maximum_score, e.evaluation_date
            FROM grade g
            JOIN evaluation e ON e.id = g.evaluation_id
            JOIN section s ON s.id = e.section_id
            WHERE e.section_id = ?
            """;

    private static final String STUDENT_GRADES = """
            SELECT g.id AS grade_id, g.evaluation_id, g.student_id,
                   e.section_id, s.course_id, g.score, e.maximum_score, e.evaluation_date
            FROM grade g
            JOIN evaluation e ON e.id = g.evaluation_id
            JOIN section s ON s.id = e.section_id
            WHERE g.student_id = ?
            """;

    public record AnalyticsGrade(
            Long id,
            Long evaluationId,
            Long studentId,
            Long sectionId,
            Long courseId,
            BigDecimal score,
            BigDecimal maximumScore,
            LocalDate evaluationDate) {

        public GradeData toGradeData() {
            return new GradeData(id, evaluationId, studentId, sectionId, courseId, score);
        }
    }
}
