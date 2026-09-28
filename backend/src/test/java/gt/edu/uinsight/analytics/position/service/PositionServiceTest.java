package gt.edu.uinsight.analytics.position.service;

import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.exception.InvalidPercentileException;
import gt.edu.uinsight.analytics.position.exception.NoGradesAvailableException;
import gt.edu.uinsight.analytics.position.exception.PositionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
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

    // Prueba 1: Cálculo de cuartiles de una sección (valores exactos)
    // Notas ordenadas: [55, 60, 67, 72, 74, 78, 85, 88, 90, 92]
    @Test
    void getSectionPosition_Success() {
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(mockGrades);
        SectionPositionResponse response = positionService.getSectionPosition(10L, null);
        assertNotNull(response);
        assertEquals(10L, response.getSectionId());
        assertEquals(10, response.getSampleSize());
        assertEquals(67.0, response.getQuartiles().get("Q1"), 0.0001);
        assertEquals(76.0, response.getQuartiles().get("Q2"), 0.0001);
        assertEquals(88.0, response.getQuartiles().get("Q3"), 0.0001);
    }

    // Prueba 2: Cálculo de percentiles opcionales (valores exactos)
    @Test
    void getSectionPosition_WithPercentiles_Success() {
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(mockGrades);
        SectionPositionResponse response = positionService.getSectionPosition(10L, Arrays.asList(25, 75));
        assertNotNull(response.getPercentiles());
        assertEquals(67.0, response.getPercentiles().get("P25"), 0.0001);
        assertEquals(88.0, response.getPercentiles().get("P75"), 0.0001);
    }

    // Prueba 3: Rechazo de regla de negocio (ID inválido en sección)
    @Test
    void getSectionPosition_InvalidId_ThrowsException() {
        assertThrows(PositionNotFoundException.class, () -> {
            positionService.getSectionPosition(-1L, null);
        });
    }

    // Prueba 4: Cálculo de percentil de estudiante (promedio real vs. notas de TODA la sección)
    @Test
    void getStudentPosition_Success() {
        List<Double> notasEstudiante = Arrays.asList(70.0, 80.0, 90.0); // promedio real = 80.0
        when(gradeIntegrationService.getGradesByStudent(5L)).thenReturn(notasEstudiante);
        when(gradeIntegrationService.getSectionIdByStudent(5L)).thenReturn(10L);
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(mockGrades);

        StudentPositionResponse response = positionService.getStudentPosition(5L);

        assertNotNull(response);
        assertEquals("EST-0005", response.getStudentCode());
        assertEquals(80.0, response.getStudentAverage(), 0.0001);
        // 6 de las 10 notas de la seccion son <= 80.0 -> percentil 60
        assertEquals(60, response.getPercentile());
    }

    // Prueba 5: Rechazo de regla de negocio (ID inválido en estudiante)
    @Test
    void getStudentPosition_InvalidId_ThrowsException() {
        assertThrows(PositionNotFoundException.class, () -> {
            positionService.getStudentPosition(0L);
        });
    }

    // Prueba 6: Lista de notas vacía en sección -> error de negocio controlado, no IndexOutOfBounds
    @Test
    void getSectionPosition_EmptyGrades_ThrowsNoGradesAvailableException() {
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(Collections.emptyList());
        assertThrows(NoGradesAvailableException.class, () -> {
            positionService.getSectionPosition(10L, null);
        });
    }

    // Prueba 7: Lista de notas vacía del estudiante -> error de negocio controlado
    @Test
    void getStudentPosition_EmptyGrades_ThrowsNoGradesAvailableException() {
        when(gradeIntegrationService.getGradesByStudent(5L)).thenReturn(Collections.emptyList());
        assertThrows(NoGradesAvailableException.class, () -> {
            positionService.getStudentPosition(5L);
        });
    }

    // Prueba 8: Percentil solicitado fuera de rango (1-99) -> error de validacion
    @Test
    void getSectionPosition_InvalidPercentile_ThrowsInvalidPercentileException() {
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(mockGrades);
        assertThrows(InvalidPercentileException.class, () -> {
            positionService.getSectionPosition(10L, Arrays.asList(100));
        });
    }

    // Prueba 9: Percentil solicitado menor a 1 -> error de validacion
    @Test
    void getSectionPosition_PercentileBelowMinimum_ThrowsInvalidPercentileException() {
        when(gradeIntegrationService.getGradesBySection(10L)).thenReturn(mockGrades);
        assertThrows(InvalidPercentileException.class, () -> {
            positionService.getSectionPosition(10L, Arrays.asList(0));
        });
    }
}