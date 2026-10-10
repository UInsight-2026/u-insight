package gt.edu.uinsight.analytics.centraltendency.service;

import gt.edu.uinsight.analytics.centraltendency.calculator.CentralTendencyCalculator;
import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.centraltendency.exception.GradeDataIntegrationException;
import gt.edu.uinsight.analytics.centraltendency.exception.InvalidAnalyticsRequestException;
import gt.edu.uinsight.analytics.centraltendency.model.GradeData;
import gt.edu.uinsight.analytics.centraltendency.repository.GradeDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CentralTendencyServiceTest {

    @Mock
    private GradeDataRepository repository;

    private CentralTendencyService service;

    @BeforeEach
    void setUp() {
        service = new CentralTendencyService(repository, new CentralTendencyCalculator());
    }

    @Test
    void calculatesASectionUsingTheEvaluationFilter() {
        when(repository.findBySectionId(10L, 45L)).thenReturn(List.of(
                grade(1L, 45L, 10L, 5L, "70"),
                grade(2L, 45L, 10L, 5L, "80")
        ));

        CentralTendencyResponse response = service.getSectionCentralTendency(10L, 45L);

        assertEquals(2, response.sampleSize());
        assertEquals(75.0, response.mean());
        verify(repository).findBySectionId(10L, 45L);
    }

    @Test
    void calculatesACourseUsingThePeriodFilter() {
        when(repository.findByCourseId(5L, 2L)).thenReturn(List.of(
                grade(1L, 11L, 10L, 5L, "60"),
                grade(2L, 12L, 11L, 5L, "90")
        ));

        CentralTendencyResponse response = service.getCourseCentralTendency(5L, 2L);

        assertEquals(75.0, response.mean());
        verify(repository).findByCourseId(5L, 2L);
    }

    @Test
    void returnsAnEmptyResultWhenTheRepositoryHasNoGrades() {
        when(repository.findBySectionId(10L, null)).thenReturn(List.of());

        CentralTendencyResponse response = service.getSectionCentralTendency(10L, null);

        assertEquals(CentralTendencyResponse.empty(), response);
    }

    @Test
    void returnsAnEmptyResultWhenTheRepositoryReturnsNull() {
        when(repository.findBySectionId(10L, null)).thenReturn(null);

        CentralTendencyResponse response = service.getSectionCentralTendency(10L, null);

        assertEquals(CentralTendencyResponse.empty(), response);
    }

    @Test
    void rejectsANonPositiveSectionIdBeforeCallingTheRepository() {
        assertThrows(
                InvalidAnalyticsRequestException.class,
                () -> service.getSectionCentralTendency(0L, null)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsANonPositiveEvaluationIdBeforeCallingTheRepository() {
        assertThrows(
                InvalidAnalyticsRequestException.class,
                () -> service.getSectionCentralTendency(10L, -1L)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsANonPositiveCourseIdBeforeCallingTheRepository() {
        assertThrows(
                InvalidAnalyticsRequestException.class,
                () -> service.getCourseCentralTendency(-5L, null)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsANonPositivePeriodIdBeforeCallingTheRepository() {
        assertThrows(
                InvalidAnalyticsRequestException.class,
                () -> service.getCourseCentralTendency(5L, 0L)
        );

        verifyNoInteractions(repository);
    }

    @Test
    void propagatesAnIntegrationFailureFromTheRepository() {
        GradeDataIntegrationException failure = new GradeDataIntegrationException(
                "No fue posible consultar la informacion de la celula A6"
        );
        when(repository.findBySectionId(10L, null)).thenThrow(failure);

        GradeDataIntegrationException thrown = assertThrows(
                GradeDataIntegrationException.class,
                () -> service.getSectionCentralTendency(10L, null)
        );

        assertSame(failure, thrown);
        verify(repository).findBySectionId(10L, null);
    }

    private GradeData grade(
            Long gradeId,
            Long evaluationId,
            Long sectionId,
            Long courseId,
            String score) {
        return new GradeData(
                gradeId,
                evaluationId,
                100L + gradeId,
                sectionId,
                courseId,
                new BigDecimal(score)
        );
    }
}
