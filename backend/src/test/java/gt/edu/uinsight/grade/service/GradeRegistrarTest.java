package gt.edu.uinsight.grade.service;

import gt.edu.uinsight.enrollment.service.EnrollmentService;
import gt.edu.uinsight.evaluation.api.EvaluationQueryService;
import gt.edu.uinsight.evaluation.api.EvaluationSummary;
import gt.edu.uinsight.grade.dto.GradeRequest;
import gt.edu.uinsight.grade.exception.DuplicateGradeException;
import gt.edu.uinsight.grade.exception.InvalidRequestException;
import gt.edu.uinsight.grade.exception.InvalidScoreException;
import gt.edu.uinsight.grade.exception.ResourceNotFoundException;
import gt.edu.uinsight.grade.exception.StudentNotEnrolledException;
import gt.edu.uinsight.grade.model.Grade;
import gt.edu.uinsight.grade.model.GradeStatus;
import gt.edu.uinsight.grade.repository.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeRegistrarTest {

    private static final Long EVALUATION_ID = 17L;
    private static final Long STUDENT_ID = 23L;
    private static final Long SECTION_ID = 31L;

    @Mock private GradeRepository gradeRepository;
    @Mock private EvaluationQueryService evaluationQueryService;
    @Mock private EnrollmentService enrollmentService;

    private GradeRegistrar registrar;

    @BeforeEach
    void setUp() {
        registrar = new GradeRegistrar(gradeRepository, evaluationQueryService, enrollmentService);
    }

    private void stubEvaluationAndEnrollment() {
        when(evaluationQueryService.findSummary(EVALUATION_ID)).thenReturn(Optional.of(
                new EvaluationSummary(EVALUATION_ID, SECTION_ID, "Parcial", new BigDecimal("100.00"),
                        new BigDecimal("30.00"), "ACTIVE")));
        when(enrollmentService.isStudentEnrolled(STUDENT_ID, SECTION_ID)).thenReturn(true);
    }

    @Test
    void registerUsesEvaluationAndEnrollmentServicesAndPersistsGrade() {
        stubEvaluationAndEnrollment();
        when(gradeRepository.existsByEvaluationIdAndStudentId(EVALUATION_ID, STUDENT_ID)).thenReturn(false);
        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> {
            Grade grade = invocation.getArgument(0);
            grade.setId(99L);
            return grade;
        });

        var response = registrar.register(request("88.50"));

        assertThat(response.getId()).isEqualTo(99L);
        assertThat(response.getEvaluationId()).isEqualTo(EVALUATION_ID);
        assertThat(response.getStudentId()).isEqualTo(STUDENT_ID);
        assertThat(response.getScore()).isEqualByComparingTo("88.50");
        assertThat(response.getStatus()).isEqualTo(GradeStatus.REGISTERED);
        assertThat(response.getRegisteredAt()).isBeforeOrEqualTo(LocalDateTime.now());
        verify(evaluationQueryService).findSummary(EVALUATION_ID);
        verify(enrollmentService).isStudentEnrolled(STUDENT_ID, SECTION_ID);
    }

    @Test
    void registerRejectsEvaluationMissingFromA5() {
        when(evaluationQueryService.findSummary(EVALUATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrar.register(request("88.50")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(EVALUATION_ID.toString());
    }

    @Test
    void registerRejectsEvaluationThatIsNotActive() {
        when(evaluationQueryService.findSummary(EVALUATION_ID)).thenReturn(Optional.of(
                new EvaluationSummary(EVALUATION_ID, SECTION_ID, "Parcial", new BigDecimal("100.00"),
                        new BigDecimal("30.00"), "DRAFT")));

        assertThatThrownBy(() -> registrar.register(request("88.50")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("ACTIVE");
    }

    @Test
    void registerRejectsStudentNotEnrolledAccordingToA4() {
        stubEvaluationAndEnrollment();
        when(enrollmentService.isStudentEnrolled(STUDENT_ID, SECTION_ID)).thenReturn(false);

        assertThatThrownBy(() -> registrar.register(request("88.50")))
                .isInstanceOf(StudentNotEnrolledException.class);
    }

    @Test
    void registerRejectsScoreAboveA5Maximum() {
        stubEvaluationAndEnrollment();
        assertThatThrownBy(() -> registrar.register(request("100.01")))
                .isInstanceOf(InvalidScoreException.class);
    }

    @Test
    void registerRejectsDuplicateGradeInCanonicalRepository() {
        stubEvaluationAndEnrollment();
        when(gradeRepository.existsByEvaluationIdAndStudentId(EVALUATION_ID, STUDENT_ID)).thenReturn(true);

        assertThatThrownBy(() -> registrar.register(request("88.50")))
                .isInstanceOf(DuplicateGradeException.class);
    }

    private GradeRequest request(String score) {
        GradeRequest request = new GradeRequest();
        request.setEvaluationId(EVALUATION_ID);
        request.setStudentId(STUDENT_ID);
        request.setScore(new BigDecimal(score));
        return request;
    }
}
