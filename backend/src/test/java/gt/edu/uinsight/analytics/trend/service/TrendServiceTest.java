// Celula B4 - Evolucion y Tendencia
package gt.edu.uinsight.analytics.trend.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.entity.Evaluation;
import gt.edu.uinsight.analytics.trend.entity.Grade;
import gt.edu.uinsight.analytics.trend.entity.Status;
import gt.edu.uinsight.analytics.trend.entity.Type;
import gt.edu.uinsight.analytics.trend.mapper.TrendMapper;
import gt.edu.uinsight.analytics.trend.repository.EvaluationRepository;
import gt.edu.uinsight.analytics.trend.repository.GradeRepository;
import gt.edu.uinsight.analytics.trend.repository.SectionRepository;
import jakarta.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrendServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private TrendMapper trendMapper;

    @InjectMocks
    private TrendService trendService;

    @Test
    void getTrendBySectionId_debeLanzarEntityNotFound_cuandoLaSeccionNoExiste() {
        when(sectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trendService.getTrendBySectionId(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getTrendByStudentId_debeRetornarInsufficientData_cuandoElEstudianteTieneMenosDeDosNotas() {
        Grade unicaNota = new Grade(1L, 5L, 78.0, "2026-01-15", "REGISTERED");
        when(gradeRepository.findByStudentId(5L))
                .thenReturn(List.of(unicaNota));
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(
                new Evaluation(10L, "E1", Type.EXAM, "2026-01-15", 100.0, 1.0, Status.CLOSED)));
        when(trendMapper.toTrendResponse(any(), any())).thenAnswer(invocation -> {
            TrendCalculator.Result result = invocation.getArgument(0);
            return new TrendResponse(result.classification(), result.averageChange(), List.of());
        });

        TrendResponse response = trendService.getTrendByStudentId(5L);

        assertThat(response.clasification()).isEqualTo(TrendClassification.INSUFFICIENT_DATA);
    }

    @Test
    void getTrendByStudentId_debeClasificarComoNegative_conElEjemploDelDocumento() {
        List<Grade> notas = List.of(
                new Grade(1L, 5L, 78.0, "2026-01-15", "REGISTERED"),
                new Grade(2L, 5L, 72.0, "2026-02-15", "REGISTERED"),
                new Grade(3L, 5L, 68.0, "2026-03-15", "REGISTERED")
        );
        when(gradeRepository.findByStudentId(5L)).thenReturn(notas);
        when(evaluationRepository.findById(any())).thenAnswer(invocation ->
                Optional.of(new Evaluation(10L, "E", Type.EXAM, "2026-01-01", 100.0, 1.0, Status.CLOSED)));
        when(trendMapper.toTrendResponse(any(), any())).thenAnswer(invocation -> {
            TrendCalculator.Result result = invocation.getArgument(0);
            return new TrendResponse(result.classification(), result.averageChange(), List.of());
        });

        TrendResponse response = trendService.getTrendByStudentId(5L);

        assertThat(response.clasification()).isEqualTo(TrendClassification.NEGATIVE);
        assertThat(response.averageChange()).isEqualByComparingTo("-5.00");
    }
}
