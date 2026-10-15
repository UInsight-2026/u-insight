package gt.edu.uinsight.analytics.trend.service;

import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository.AnalyticsGrade;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.mapper.TrendMapper;
import gt.edu.uinsight.evaluation.api.EvaluationSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrendServiceTest {

    @Mock
    private AnalyticsDatabaseRepository analyticsRepository;

    @Spy
    private TrendMapper trendMapper = new TrendMapper();

    private TrendService trendService;

    @BeforeEach
    void setUp() {
        trendService = new TrendService(trendMapper, analyticsRepository);
        ReflectionTestUtils.setField(trendService, "negativeThreshold", new BigDecimal("-3"));
        ReflectionTestUtils.setField(trendService, "positiveThreshold", new BigDecimal("3"));
    }

    private AnalyticsGrade grade(Long id, Long evaluationId, int score, String date) {
        return new AnalyticsGrade(id, evaluationId, 5L, 10L, 1L,
                BigDecimal.valueOf(score), BigDecimal.valueOf(100), LocalDate.parse(date));
    }

    private EvaluationSummary evaluation(Long id, String date) {
        return new EvaluationSummary(id, 10L, "E" + id, BigDecimal.valueOf(100),
                BigDecimal.ONE, "ACTIVE", LocalDate.parse(date));
    }

    @Test
    void getTrendBySectionId_rejectsUnknownSection() {
        when(analyticsRepository.sectionExists(99L)).thenReturn(false);

        assertThatThrownBy(() -> trendService.getTrendBySectionId(99L))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }

    @Test
    void getTrendByStudentId_rejectsUnknownStudent() {
        when(analyticsRepository.studentExists(99L)).thenReturn(false);

        assertThatThrownBy(() -> trendService.getTrendByStudentId(99L))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class);
    }

    @Test
    void getTrendByStudentId_returnsInsufficientDataForSingleGrade() {
        when(analyticsRepository.studentExists(5L)).thenReturn(true);
        when(analyticsRepository.findGradesByStudent(5L))
                .thenReturn(List.of(grade(1L, 1L, 78, "2026-09-01")));
        when(analyticsRepository.findEvaluationSummary(1L))
                .thenReturn(Optional.of(evaluation(1L, "2026-09-01")));

        TrendResponse response = trendService.getTrendByStudentId(5L);

        assertThat(response.classification()).isEqualTo(TrendClassification.INSUFFICIENT_DATA);
    }

    @Test
    void getTrendByStudentId_classifiesNegativeTrend() {
        when(analyticsRepository.studentExists(5L)).thenReturn(true);
        when(analyticsRepository.findGradesByStudent(5L)).thenReturn(List.of(
                grade(1L, 1L, 78, "2026-09-01"),
                grade(2L, 2L, 72, "2026-09-08"),
                grade(3L, 3L, 68, "2026-09-15")));
        when(analyticsRepository.findEvaluationSummary(1L))
                .thenReturn(Optional.of(evaluation(1L, "2026-09-01")));
        when(analyticsRepository.findEvaluationSummary(2L))
                .thenReturn(Optional.of(evaluation(2L, "2026-09-08")));
        when(analyticsRepository.findEvaluationSummary(3L))
                .thenReturn(Optional.of(evaluation(3L, "2026-09-15")));

        TrendResponse response = trendService.getTrendByStudentId(5L);

        assertThat(response.classification()).isEqualTo(TrendClassification.NEGATIVE);
        assertThat(response.averageChange()).isEqualByComparingTo("-5.00");
        assertThat(response.points()).hasSize(3);
    }

    @Test
    void getTrendBySectionId_ordersPointsByEvaluationDate() {
        when(analyticsRepository.sectionExists(10L)).thenReturn(true);
        when(analyticsRepository.findGradesBySection(10L)).thenReturn(List.of(
                grade(20L, 20L, 90, "2026-09-15"),
                grade(10L, 10L, 80, "2026-09-01")));
        when(analyticsRepository.findEvaluationSummary(20L))
                .thenReturn(Optional.of(evaluation(20L, "2026-09-15")));
        when(analyticsRepository.findEvaluationSummary(10L))
                .thenReturn(Optional.of(evaluation(10L, "2026-09-01")));

        TrendResponse response = trendService.getTrendBySectionId(10L);

        assertThat(response.classification()).isEqualTo(TrendClassification.POSITIVE);
        assertThat(response.points().stream().map(point -> point.label()).toList())
                .containsExactly("E1", "E2");
        assertThat(response.points().stream().map(point -> point.value()).toList())
                .containsExactly(80, 90);
    }
}
