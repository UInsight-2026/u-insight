package gt.edu.uinsight.analytics.position.service;

import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.exception.AmbiguousSectionException;
import gt.edu.uinsight.analytics.position.exception.NoGradesAvailableException;
import gt.edu.uinsight.analytics.position.exception.PositionNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private GradeIntegrationService gradeIntegrationService;
    @InjectMocks
    private PositionService positionService;

    @Test
    void calculatesSectionQuartilesAndRequestedPercentiles() {
        when(gradeIntegrationService.getGradesBySection(10L))
                .thenReturn(List.of(60.0, 72.0, 85.0, 90.0, 55.0, 78.0, 88.0, 92.0, 67.0, 74.0));

        var response = positionService.getSectionPosition(10L, List.of(25, 75));

        assertEquals(10, response.getSampleSize());
        assertEquals(67.0, response.getQuartiles().get("Q1"));
        assertEquals(76.0, response.getQuartiles().get("Q2"));
        assertEquals(88.0, response.getQuartiles().get("Q3"));
        assertEquals(67.0, response.getPercentiles().get("P25"));
        assertEquals(88.0, response.getPercentiles().get("P75"));
    }

    @Test
    void calculatesStudentAverageWithinExplicitlySelectedSection() {
        when(gradeIntegrationService.isStudentEnrolledInSection(5L, 20L)).thenReturn(true);
        when(gradeIntegrationService.getGradesByStudent(5L, 20L))
                .thenReturn(List.of(70.0, 90.0));
        when(gradeIntegrationService.getStudentCode(5L)).thenReturn("EST2024005");
        when(gradeIntegrationService.getGradesBySection(20L))
                .thenReturn(List.of(60.0, 70.0, 80.0, 90.0, 100.0));

        StudentPositionResponse response = positionService.getStudentPosition(5L, 20L);

        assertEquals("EST2024005", response.getStudentCode());
        assertEquals(80.0, response.getStudentAverage());
        assertEquals(60, response.getPercentile());
    }

    @Test
    void resolvesTheOnlyActiveSectionForStudent() {
        when(gradeIntegrationService.getSectionIdsByStudent(5L)).thenReturn(List.of(10L));
        when(gradeIntegrationService.getGradesByStudent(5L, 10L)).thenReturn(List.of(70.0, 90.0));
        when(gradeIntegrationService.getStudentCode(5L)).thenReturn("EST2024005");
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(List.of(70.0, 90.0));

        assertEquals(80.0, positionService.getStudentPosition(5L, null).getStudentAverage());
    }

    @Test
    void requiresSectionWhenStudentHasMultipleActiveSections() {
        when(gradeIntegrationService.getSectionIdsByStudent(5L)).thenReturn(List.of(10L, 20L));

        assertThrows(AmbiguousSectionException.class,
                () -> positionService.getStudentPosition(5L, null));
    }

    @Test
    void rejectsSectionWhenStudentIsNotEnrolled() {
        when(gradeIntegrationService.isStudentEnrolledInSection(5L, 99L)).thenReturn(false);

        assertThrows(PositionNotFoundException.class,
                () -> positionService.getStudentPosition(5L, 99L));
    }

    @Test
    void reportsWhenSelectedSectionHasNoGrades() {
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(List.of());

        assertThrows(NoGradesAvailableException.class,
                () -> positionService.getSectionPosition(10L, null));
    }
}
