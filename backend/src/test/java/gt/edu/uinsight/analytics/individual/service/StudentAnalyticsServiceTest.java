package gt.edu.uinsight.analytics.individual.service;

import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.exception.StudentNotFoundException;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.service.PositionService;
import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import gt.edu.uinsight.analytics.trend.service.TrendService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentAnalyticsServiceTest {

    @Mock
    private PositionService positionService;
    @Mock
    private TrendService trendService;
    @Mock
    private AnalyticsDatabaseRepository databaseRepository;
    @InjectMocks
    private StudentAnalyticsService service;

    @Test
    void summaryReadsStudentAndSectionAveragesFromDatabase() {
        when(databaseRepository.studentExists(1L)).thenReturn(true);
        when(databaseRepository.findStudentCode(1L)).thenReturn("EST2024001");
        when(databaseRepository.findEnrolledSectionIdsByStudent(1L)).thenReturn(List.of(5L));
        when(databaseRepository.findGradesByStudentAndSection(1L, 5L))
                .thenReturn(List.of(grade(70), grade(90)));
        when(databaseRepository.findGradesBySection(5L))
                .thenReturn(List.of(grade(70), grade(90), grade(80)));

        StudentSummaryResponse response = service.getSummary(1L, null);

        assertEquals("EST2024001", response.studentCode());
        assertEquals(new BigDecimal("80.0"), response.studentAverage());
        assertEquals(new BigDecimal("80.0"), response.sectionAverage());
        assertEquals(BigDecimal.ZERO.setScale(1), response.difference());
    }

    @Test
    void summaryRequiresSectionWhenStudentHasMultipleActiveEnrollments() {
        when(databaseRepository.studentExists(1L)).thenReturn(true);
        when(databaseRepository.findEnrolledSectionIdsByStudent(1L)).thenReturn(List.of(5L, 6L));

        assertThrows(ResponseStatusException.class, () -> service.getSummary(1L, null));
    }

    @Test
    void comparisonUsesPositionForTheSelectedSection() {
        when(databaseRepository.studentExists(1L)).thenReturn(true);
        when(databaseRepository.findStudentCode(1L)).thenReturn("EST2024001");
        when(databaseRepository.findEnrolledSectionIdsByStudent(1L)).thenReturn(List.of(5L, 6L));
        when(databaseRepository.findGradesByStudentAndSection(1L, 6L)).thenReturn(List.of(grade(85)));
        when(databaseRepository.findGradesBySection(6L)).thenReturn(List.of(grade(85), grade(75)));
        when(positionService.getStudentPosition(1L, 6L))
                .thenReturn(new StudentPositionResponse("EST2024001", 85.0, 75));

        StudentComparisonResponse response = service.getComparison(1L, 6L);

        assertEquals(75, response.percentile());
        verify(positionService).getStudentPosition(1L, 6L);
    }

    @Test
    void rejectsStudentNotPresentInDatabase() {
        when(databaseRepository.studentExists(99L)).thenReturn(false);

        assertThrows(StudentNotFoundException.class, () -> service.getSummary(99L, null));
    }

    private AnalyticsDatabaseRepository.AnalyticsGrade grade(int score) {
        return new AnalyticsDatabaseRepository.AnalyticsGrade(
                1L, 1L, 1L, 5L, 1L, BigDecimal.valueOf(score),
                BigDecimal.valueOf(100), LocalDate.of(2026, 1, 1));
    }
}
