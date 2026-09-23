package gt.edu.uinsight.analytics.position.service;

import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.exception.PositionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {

    @Mock
    private GradeIntegrationService gradeIntegrationService;

    @InjectMocks
    private PositionService positionService;

    private List<Double> mockGrades;

    @BeforeEach
    void setUp() {
        mockGrades = Arrays.asList(60.0, 72.0, 85.0, 90.0, 55.0, 78.0, 88.0, 92.0, 67.0, 74.0);
    }

    // Prueba 1: Cálculo de cuartiles de una sección
    @Test
    void getSectionPosition_Success() {
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(mockGrades);
        SectionPositionResponse response = positionService.getSectionPosition(10L, null);
        assertNotNull(response);
        assertEquals(10L, response.getSectionId());
        assertTrue(response.getQuartiles().containsKey("Q1"));
    }

    // Prueba 2: Cálculo de percentiles opcionales
    @Test
    void getSectionPosition_WithPercentiles_Success() {
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(mockGrades);
        SectionPositionResponse response = positionService.getSectionPosition(10L, Arrays.asList(25, 75));
        assertNotNull(response.getPercentiles());
        assertTrue(response.getPercentiles().containsKey("P25"));
        assertTrue(response.getPercentiles().containsKey("P75"));
    }

    // Prueba 3: Rechazo de regla de negocio (ID inválido en sección)
    @Test
    void getSectionPosition_InvalidId_ThrowsException() {
        assertThrows(PositionNotFoundException.class, () -> {
            positionService.getSectionPosition(-1L, null);
        });
    }

    // Prueba 4: Cálculo de percentil de estudiante
    @Test
    void getStudentPosition_Success() {
        when(gradeIntegrationService.getGradesByStudent(5L)).thenReturn(mockGrades);
        StudentPositionResponse response = positionService.getStudentPosition(5L);
        assertNotNull(response);
        assertEquals("EST-0005", response.getStudentCode());
    }

    // Prueba 5: Rechazo de regla de negocio (ID inválido en estudiante)
    @Test
    void getStudentPosition_InvalidId_ThrowsException() {
        assertThrows(PositionNotFoundException.class, () -> {
            positionService.getStudentPosition(0L);
        });
    }
}