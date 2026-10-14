// Celula B4 - Evolucion y Tendencia
package gt.edu.uinsight.analytics.trend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.entity.Evaluation;
import gt.edu.uinsight.analytics.trend.entity.Grade;
import gt.edu.uinsight.analytics.trend.entity.Student;
import gt.edu.uinsight.analytics.trend.mapper.TrendMapper;
import gt.edu.uinsight.analytics.trend.repository.EvaluationRepository;
import gt.edu.uinsight.analytics.trend.repository.GradeRepository;
import gt.edu.uinsight.analytics.trend.repository.SectionRepository;
import gt.edu.uinsight.analytics.trend.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class TrendServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private StudentRepository studentRepository;

    // Mapper real: es una clase sin dependencias, no hace falta simularlo.
    @Spy
    private TrendMapper trendMapper = new TrendMapper();

    @InjectMocks
    private TrendService trendService;

    @BeforeEach
    void setUp() {
        // Los umbrales llegan por @Value; en un test unitario sin Spring
        // hay que asignarlos a mano (mismos valores por defecto del servicio).
        ReflectionTestUtils.setField(trendService, "negativeThreshold", new BigDecimal("-3"));
        ReflectionTestUtils.setField(trendService, "positiveThreshold", new BigDecimal("3"));
    }

    private Grade grade(Long evaluationId, String score) {
        return new Grade(evaluationId, 5L, new BigDecimal(score), null, "ACTIVE");
    }

    @Test
    void getTrendBySectionId_debeLanzarEntityNotFound_cuandoLaSeccionNoExiste() {
        when(sectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trendService.getTrendBySectionId(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getTrendByStudentId_debeLanzarEntityNotFound_cuandoElEstudianteNoExiste() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trendService.getTrendByStudentId(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getTrendByStudentId_debeRetornarInsufficientData_cuandoElEstudianteTieneMenosDeDosNotas() {
        Student student = new Student();
        Grade unicaNota = grade(1L, "78");
        Evaluation evaluation = new Evaluation(
                10L, "E1", null, null, new BigDecimal("100"), BigDecimal.ONE, null);

        when(studentRepository.findById(5L)).thenReturn(Optional.of(student));
        when(gradeRepository.findByStudentId(5L)).thenReturn(List.of(unicaNota));
        when(evaluationRepository.findByEvaluationId(1L)).thenReturn(evaluation);

        TrendResponse response = trendService.getTrendByStudentId(5L);

        assertThat(response.classification()).isEqualTo(TrendClassification.INSUFFICIENT_DATA);
    }

    @Test
    void getTrendByStudentId_debeClasificarComoNegative_conElEjemploDelDocumento() {
        Student student = new Student();
        Grade nota1 = grade(1L, "78");
        Grade nota2 = grade(2L, "72");
        Grade nota3 = grade(3L, "68");
        Evaluation evaluation = new Evaluation(
                10L, "E1", null, null, new BigDecimal("100"), BigDecimal.ONE, null);

        when(studentRepository.findById(5L)).thenReturn(Optional.of(student));
        when(gradeRepository.findByStudentId(5L)).thenReturn(List.of(nota1, nota2, nota3));
        when(evaluationRepository.findByEvaluationId(org.mockito.ArgumentMatchers.any()))
                .thenReturn(evaluation);

        TrendResponse response = trendService.getTrendByStudentId(5L);

        // E1=78, E2=72, E3=68 -> cambios -6 y -4 -> promedio -5 -> NEGATIVE
        assertThat(response.classification()).isEqualTo(TrendClassification.NEGATIVE);
        assertThat(response.averageChange()).isEqualByComparingTo("-5.00");
        assertThat(response.points()).hasSize(3);
    }
}
