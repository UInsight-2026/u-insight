package gt.edu.uinsight.analytics.position.service;

import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseGradeIntegrationServiceTest {

    @Mock
    private AnalyticsDatabaseRepository databaseRepository;
    @InjectMocks
    private DatabaseGradeIntegrationService service;

    @Test
    void normalizesGradesAndReadsEnrollmentsAndStudentCodeFromDatabase() {
        when(databaseRepository.findGradesBySection(10L)).thenReturn(List.of(grade(15, 20)));
        when(databaseRepository.findGradesByStudentAndSection(5L, 10L)).thenReturn(List.of(grade(40, 50)));
        when(databaseRepository.findActiveSectionIdsByStudent(5L)).thenReturn(List.of(10L));
        when(databaseRepository.findStudentCode(5L)).thenReturn("EST2024005");

        assertEquals(List.of(75.0), service.getGradesBySection(10L));
        assertEquals(List.of(80.0), service.getGradesByStudent(5L, 10L));
        assertEquals(List.of(10L), service.getSectionIdsByStudent(5L));
        assertEquals("EST2024005", service.getStudentCode(5L));
    }

    private AnalyticsDatabaseRepository.AnalyticsGrade grade(int score, int maximumScore) {
        return new AnalyticsDatabaseRepository.AnalyticsGrade(
                1L, 1L, 5L, 10L, 1L, BigDecimal.valueOf(score),
                BigDecimal.valueOf(maximumScore), LocalDate.of(2026, 1, 1));
    }
}
