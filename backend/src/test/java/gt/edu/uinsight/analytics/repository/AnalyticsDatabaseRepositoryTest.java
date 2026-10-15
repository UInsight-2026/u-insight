package gt.edu.uinsight.analytics.repository;

import gt.edu.uinsight.academicperiod.service.AcademicPeriodService;
import gt.edu.uinsight.analytics.centraltendency.exception.InvalidAnalyticsRequestException;
import gt.edu.uinsight.analytics.centraltendency.repository.GradeDataRepositoryImpl;
import gt.edu.uinsight.analytics.dispersion.repository.DispersionGradeRepository;
import gt.edu.uinsight.course.service.CourseService;
import gt.edu.uinsight.enrollment.service.EnrollmentService;
import gt.edu.uinsight.evaluation.api.EvaluationQueryService;
import gt.edu.uinsight.evaluation.api.EvaluationSummary;
import gt.edu.uinsight.grade.dto.GradeResponse;
import gt.edu.uinsight.grade.service.GradeService;
import gt.edu.uinsight.section.dto.SectionResponseDTO;
import gt.edu.uinsight.section.service.SectionService;
import gt.edu.uinsight.student.api.StudentQueryService;
import gt.edu.uinsight.student.api.StudentSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalyticsDatabaseRepositoryTest {

    private SectionService sectionService;
    private CourseService courseService;
    private AcademicPeriodService periodService;
    private EvaluationQueryService evaluationService;
    private GradeService gradeService;
    private EnrollmentService enrollmentService;
    private StudentQueryService studentService;
    private AnalyticsDatabaseRepository repository;

    @BeforeEach
    void setUp() {
        sectionService = mock(SectionService.class);
        courseService = mock(CourseService.class);
        periodService = mock(AcademicPeriodService.class);
        evaluationService = mock(EvaluationQueryService.class);
        gradeService = mock(GradeService.class);
        enrollmentService = mock(EnrollmentService.class);
        studentService = mock(StudentQueryService.class);
        repository = new AnalyticsDatabaseRepository(sectionService, courseService, periodService,
                evaluationService, gradeService, enrollmentService, studentService);

        when(sectionService.existsById(100L)).thenReturn(true);
        when(sectionService.getSectionById(100L))
                .thenReturn(new SectionResponseDTO(100L, 20L, 10L, 1L, "A", "ACTIVE"));
        when(sectionService.getAllSections())
                .thenReturn(List.of(new SectionResponseDTO(100L, 20L, 10L, 1L, "A", "ACTIVE")));
        when(courseService.existsById(10L)).thenReturn(true);
        when(periodService.existsById(20L)).thenReturn(true);
        when(studentService.findSummary(1L))
                .thenReturn(Optional.of(new StudentSummary(1L, "EST2024001")));
        when(enrollmentService.isStudentEnrolled(1L, 100L)).thenReturn(true);
        when(evaluationService.findSummary(1000L)).thenReturn(Optional.of(evaluation(1000L, 100L)));
        GradeResponse grade = grade(1L, 1000L, 1L, "80.00");
        when(gradeService.getBySection(100L)).thenReturn(List.of(grade));
        when(gradeService.getByStudent(1L)).thenReturn(List.of(grade));
    }

    @Test
    void composesGradeAndReferenceDataFromOwningServices() {
        assertEquals("EST2024001", repository.findStudentCode(1L));
        assertTrue(repository.studentExists(1L));
        assertEquals(List.of(100L), repository.findEnrolledSectionIdsByStudent(1L));
        assertEquals(List.of(80.0), repository.findGradesBySection(100L).stream()
                .map(grade -> grade.score().doubleValue()).toList());
        assertEquals(List.of(80.0), repository.findGradesByCourse(10L, 20L).stream()
                .map(grade -> grade.score().doubleValue()).toList());
    }

    @Test
    void includesClosedSectionsWhenEnrollmentIsStillActive() {
        when(sectionService.getAllSections()).thenReturn(List.of(
                new SectionResponseDTO(100L, 20L, 10L, 1L, "A", "ACTIVE"),
                new SectionResponseDTO(101L, 20L, 10L, 1L, "B", "CLOSED")));
        when(enrollmentService.isStudentEnrolled(1L, 101L)).thenReturn(true);

        assertEquals(List.of(100L, 101L), repository.findEnrolledSectionIdsByStudent(1L));
    }

    @Test
    void servesCentralTendencyAndDispersionFromServiceBackedAdapter() {
        GradeDataRepositoryImpl centralTendency = new GradeDataRepositoryImpl(repository);
        DispersionGradeRepository dispersion = new DispersionGradeRepository(repository);

        assertEquals(new BigDecimal("80.00"), centralTendency.findBySectionId(100L, null)
                .getFirst().getScore());
        assertEquals(List.of(new BigDecimal("80.00")), dispersion.findScoresBySectionId(100L));
        assertTrue(dispersion.courseExists(10L));
        assertFalse(dispersion.courseExists(999L));
    }

    @Test
    void rejectsEvaluationOutsideRequestedSection() {
        when(evaluationService.findSummary(1001L)).thenReturn(Optional.of(evaluation(1001L, 101L)));
        GradeDataRepositoryImpl centralTendency = new GradeDataRepositoryImpl(repository);

        assertThrows(InvalidAnalyticsRequestException.class,
                () -> centralTendency.findBySectionId(100L, 1001L));
    }

    @Test
    void countsEnrolledStudentsBelowThresholdUsingNormalizedScores() {
        when(enrollmentService.getEnrolledStudentIds(100L)).thenReturn(List.of(1L));
        assertEquals(1, repository.countStudentsBelowAverage(100L, 81));
        assertEquals(0, repository.countStudentsBelowAverage(100L, 80));
    }

    private EvaluationSummary evaluation(long id, long sectionId) {
        return new EvaluationSummary(id, sectionId, "Examen", new BigDecimal("100.00"),
                BigDecimal.ONE, "ACTIVE", LocalDate.parse("2026-01-10"));
    }

    private GradeResponse grade(long id, long evaluationId, long studentId, String score) {
        GradeResponse response = mock(GradeResponse.class);
        when(response.getId()).thenReturn(id);
        when(response.getEvaluationId()).thenReturn(evaluationId);
        when(response.getStudentId()).thenReturn(studentId);
        when(response.getScore()).thenReturn(new BigDecimal(score));
        return response;
    }
}
