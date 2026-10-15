package gt.edu.uinsight.analytics.repository;

import gt.edu.uinsight.academicperiod.service.AcademicPeriodService;
import gt.edu.uinsight.analytics.centraltendency.model.GradeData;
import gt.edu.uinsight.academicperiod.support.exception.AcademicResourceNotFoundException;
import gt.edu.uinsight.course.service.CourseService;
import gt.edu.uinsight.enrollment.service.EnrollmentService;
import gt.edu.uinsight.evaluation.api.EvaluationQueryService;
import gt.edu.uinsight.evaluation.api.EvaluationSummary;
import gt.edu.uinsight.grade.dto.GradeResponse;
import gt.edu.uinsight.grade.service.GradeService;
import gt.edu.uinsight.section.dto.SectionResponseDTO;
import gt.edu.uinsight.section.service.SectionService;
import gt.edu.uinsight.student.api.StudentQueryService;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

/**
 * Analytics adapter that composes the owning A services instead of reading
 * their tables independently.
 */
@Repository
public class AnalyticsDatabaseRepository {

    private final SectionService sectionService;
    private final CourseService courseService;
    private final AcademicPeriodService academicPeriodService;
    private final EvaluationQueryService evaluationQueryService;
    private final GradeService gradeService;
    private final EnrollmentService enrollmentService;
    private final StudentQueryService studentQueryService;

    public AnalyticsDatabaseRepository(
            SectionService sectionService,
            CourseService courseService,
            AcademicPeriodService academicPeriodService,
            EvaluationQueryService evaluationQueryService,
            GradeService gradeService,
            EnrollmentService enrollmentService,
            StudentQueryService studentQueryService) {
        this.sectionService = sectionService;
        this.courseService = courseService;
        this.academicPeriodService = academicPeriodService;
        this.evaluationQueryService = evaluationQueryService;
        this.gradeService = gradeService;
        this.enrollmentService = enrollmentService;
        this.studentQueryService = studentQueryService;
    }

    public boolean sectionExists(Long sectionId) {
        return sectionId != null && sectionService.existsById(sectionId);
    }

    public boolean courseExists(Long courseId) {
        return courseId != null && courseService.existsById(courseId);
    }

    public boolean periodExists(Long periodId) {
        return periodId != null && academicPeriodService.existsById(periodId);
    }

    public boolean evaluationBelongsToSection(Long evaluationId, Long sectionId) {
        return evaluationId != null && evaluationQueryService.findSummary(evaluationId)
                .map(evaluation -> evaluation.sectionId().equals(sectionId))
                .orElse(false);
    }

    public java.util.Optional<EvaluationSummary> findEvaluationSummary(Long evaluationId) {
        return evaluationQueryService.findSummary(evaluationId);
    }

    public boolean studentExists(Long studentId) {
        return studentId != null && studentQueryService.findSummary(studentId).isPresent();
    }

    public String findStudentCode(Long studentId) {
        return studentQueryService.findSummary(studentId)
                .map(summary -> summary.studentCode())
                .orElse(null);
    }

    public List<AnalyticsGrade> findGradesBySection(Long sectionId) {
        requireSection(sectionId);
        return gradeService.getBySection(sectionId).stream()
                .map(this::toAnalyticsGrade)
                .sorted(gradeOrder())
                .toList();
    }

    public List<AnalyticsGrade> findGradesBySectionAndEvaluation(Long sectionId, Long evaluationId) {
        return findGradesBySection(sectionId).stream()
                .filter(grade -> grade.evaluationId().equals(evaluationId))
                .toList();
    }

    public List<AnalyticsGrade> findGradesByCourse(Long courseId, Long periodId) {
        if (!courseExists(courseId)) {
            throw new AcademicResourceNotFoundException("No existe un curso con id " + courseId);
        }
        if (periodId != null && !periodExists(periodId)) {
            throw new AcademicResourceNotFoundException(
                    "No existe un periodo academico con id " + periodId);
        }
        return sectionService.getAllSections().stream()
                .filter(section -> courseId.equals(section.getCourseId()))
                .filter(section -> "ACTIVE".equalsIgnoreCase(section.getStatus()))
                .filter(section -> periodId == null || periodId.equals(section.getAcademicPeriodId()))
                .flatMap(section -> gradeService.getBySection(section.getId()).stream()
                        .map(this::toAnalyticsGrade))
                .sorted(gradeOrder())
                .toList();
    }

    public List<AnalyticsGrade> findGradesByStudent(Long studentId) {
        requireStudent(studentId);
        return gradeService.getByStudent(studentId).stream()
                .map(this::toAnalyticsGrade)
                .sorted(gradeOrder())
                .toList();
    }

    public List<AnalyticsGrade> findGradesByStudentAndSection(Long studentId, Long sectionId) {
        requireStudent(studentId);
        requireSection(sectionId);
        return gradeService.getByStudent(studentId).stream()
                .map(this::toAnalyticsGrade)
                .filter(grade -> sectionId.equals(grade.sectionId()))
                .sorted(gradeOrder())
                .toList();
    }

    public List<Long> findEnrolledSectionIdsByStudent(Long studentId) {
        requireStudent(studentId);
        return sectionService.getAllSections().stream()
                .filter(section -> enrollmentService.isStudentEnrolled(studentId, section.getId()))
                .map(SectionResponseDTO::getId)
                .sorted()
                .toList();
    }

    public BigDecimal averageScoreForStudent(Long studentId, Long sectionId) {
        return average(findGradesByStudentAndSection(studentId, sectionId));
    }

    public BigDecimal averageScoreForSection(Long sectionId) {
        return average(findGradesBySection(sectionId));
    }

    public int countStudentsBelowAverage(Long sectionId, double threshold) {
        requireSection(sectionId);
        java.util.Set<Long> enrolledStudentIds = new java.util.HashSet<>(
                enrollmentService.getEnrolledStudentIds(sectionId));
        java.util.Map<Long, List<AnalyticsGrade>> gradesByStudent =
                findGradesBySection(sectionId).stream()
                        .collect(java.util.stream.Collectors.groupingBy(AnalyticsGrade::studentId));
        int count = 0;
        for (var entry : gradesByStudent.entrySet()) {
            if (enrolledStudentIds.contains(entry.getKey())
                    && averageNormalizedScore(entry.getValue()).doubleValue() < threshold) {
                count++;
            }
        }
        return count;
    }

    private AnalyticsGrade toAnalyticsGrade(GradeResponse grade) {
        EvaluationSummary evaluation = evaluationQueryService.findSummary(grade.getEvaluationId())
                .orElseThrow(() -> new IllegalStateException(
                        "La evaluación " + grade.getEvaluationId() + " de una calificación no existe."));
        SectionResponseDTO section = sectionService.getSectionById(evaluation.sectionId());
        return new AnalyticsGrade(
                grade.getId(), grade.getEvaluationId(), grade.getStudentId(),
                evaluation.sectionId(), section.getCourseId(), grade.getScore(),
                evaluation.maximumScore(), evaluation.evaluationDate());
    }

    private BigDecimal averageNormalizedScore(List<AnalyticsGrade> grades) {
        BigDecimal total = BigDecimal.ZERO;
        for (AnalyticsGrade grade : grades) {
            if (grade.score() == null || grade.maximumScore() == null
                    || grade.maximumScore().signum() <= 0) {
                throw new IllegalStateException(
                        "La calificación y su nota máxima deben ser válidas para calcular el promedio.");
            }
            total = total.add(grade.score()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(grade.maximumScore(), 6, RoundingMode.HALF_UP));
        }
        return total.divide(BigDecimal.valueOf(grades.size()), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal average(List<AnalyticsGrade> grades) {
        if (grades.isEmpty()) {
            return null;
        }
        BigDecimal total = grades.stream()
                .map(AnalyticsGrade::score)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(grades.size()), 1, RoundingMode.HALF_UP);
    }

    private Comparator<AnalyticsGrade> gradeOrder() {
        return Comparator.comparing(AnalyticsGrade::evaluationDate,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(AnalyticsGrade::id);
    }

    private void requireSection(Long sectionId) {
        if (!sectionExists(sectionId)) {
            throw new IllegalArgumentException("La sección " + sectionId + " no existe.");
        }
    }

    private void requireStudent(Long studentId) {
        if (!studentExists(studentId)) {
            throw new IllegalArgumentException("El estudiante " + studentId + " no existe.");
        }
    }

    public record AnalyticsGrade(
            Long id,
            Long evaluationId,
            Long studentId,
            Long sectionId,
            Long courseId,
            BigDecimal score,
            BigDecimal maximumScore,
            java.time.LocalDate evaluationDate) {

        public GradeData toGradeData() {
            return new GradeData(id, evaluationId, studentId, sectionId, courseId, score);
        }
    }
}
