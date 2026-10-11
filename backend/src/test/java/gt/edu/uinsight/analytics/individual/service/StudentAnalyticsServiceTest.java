// Celula B5 - Analisis Individual | Pruebas unitarias de StudentAnalyticsService
package gt.edu.uinsight.analytics.individual.service;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.centraltendency.service.CentralTendencyService;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentTrendResponse;
import gt.edu.uinsight.analytics.individual.exception.StudentNotFoundException;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.service.PositionService;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;
import gt.edu.uinsight.analytics.trend.service.TrendService;
import gt.edu.uinsight.enrollment.entity.Enrollment;
import gt.edu.uinsight.enrollment.repository.EnrollmentRepository;
import gt.edu.uinsight.grade.dto.GradeResponse;
import gt.edu.uinsight.grade.service.GradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentAnalyticsServiceTest {

    @Mock
    private CentralTendencyService centralTendencyService;

    @Mock
    private PositionService positionService;

    @Mock
    private GradeService gradeService;

    @Mock
    private TrendService trendService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private StudentAnalyticsService service;

    // ---------- Metodos de apoyo ----------

    private GradeResponse nota(String score) {
        return mock(GradeResponse.class, invocation -> "getScore".equals(invocation.getMethod().getName()) ? new BigDecimal(score) : null);
    }

    private void inscribir(Long studentId, Long sectionId) {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getSectionId()).thenReturn(sectionId);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(List.of(enrollment));
    }

    private void promedioSeccion(double mean) {
        when(centralTendencyService.getSectionCentralTendency(anyLong(), eq(null)))
                .thenReturn(new CentralTendencyResponse(5, mean, 70.0, List.of(65.0)));
    }

    // ---------- Pruebas 1 a 5 (Andrea, adaptadas a datos reales) ----------

    // Prueba 1: getSummary con un id valido devuelve el resumen correcto
    @Test
    void getSummary_debeRetornarResumen_cuandoIdEsValido() {
        when(gradeService.getByStudent(1L)).thenReturn(List.of(nota("50"), nota("66")));
        inscribir(1L, 10L);
        promedioSeccion(72.0);

        StudentSummaryResponse response = service.getSummary(1L);

        assertNotNull(response);
        assertEquals("EST-0001", response.studentCode());
        assertEquals(0, new BigDecimal("58.0").compareTo(response.studentAverage()));
        assertEquals(0, new BigDecimal("72.0").compareTo(response.sectionAverage()));
        assertEquals(0, new BigDecimal("-14.0").compareTo(response.difference()));
    }

    // Prueba 2: getComparison con un id valido devuelve la comparacion y el percentil
    @Test
    void getComparison_debeRetornarComparacion_cuandoIdEsValido() {
        when(gradeService.getByStudent(25L)).thenReturn(List.of(nota("58")));
        inscribir(25L, 10L);
        promedioSeccion(72.0);
        when(positionService.getStudentPosition(25L))
                .thenReturn(new StudentPositionResponse("EST-0025", 58.0, 20));

        StudentComparisonResponse response = service.getComparison(25L);

        assertNotNull(response);
        assertEquals("EST-0025", response.studentCode());
        assertEquals(0, new BigDecimal("-14.0").compareTo(response.difference()));
        assertEquals(20, response.percentile());
    }

    // Prueba 3: getTrend con un id valido devuelve la tendencia real de B4
    @Test
    void getTrend_debeRetornarTendencia_cuandoIdEsValido() {
        when(trendService.getTrendByStudentId(7L))
                .thenReturn(new TrendResponse(TrendClassification.NEGATIVE, new BigDecimal("-5"), List.of()));

        StudentTrendResponse response = service.getTrend(7L);

        assertNotNull(response);
        assertEquals("EST-0007", response.studentCode());
        assertEquals("NEGATIVE", response.trend());
        assertEquals(-5.0, response.averageChange());
    }

    // Prueba 4: getSummary con id 0 lanza StudentNotFoundException
    @Test
    void getSummary_debeLanzarExcepcion_cuandoIdEsCero() {
        StudentNotFoundException exception = assertThrows(
                StudentNotFoundException.class,
                () -> service.getSummary(0L)
        );

        assertEquals("No se encontró el estudiante con id: 0", exception.getMessage());
    }

    // Prueba 5: getComparison y getTrend con id negativo o null lanzan StudentNotFoundException
    @Test
    void getComparisonYGetTrend_debenLanzarExcepcion_cuandoIdEsInvalido() {
        assertThrows(StudentNotFoundException.class, () -> service.getComparison(-5L));
        assertThrows(StudentNotFoundException.class, () -> service.getTrend(null));
    }

    // ---------- Pruebas 6 a 12 (nuevas: casos limite y fallbacks) ----------

    // Prueba 6: la diferencia es positiva cuando el estudiante supera a su seccion
    @Test
    void getSummary_debeRetornarDiferenciaPositiva_cuandoEstudianteSuperaLaSeccion() {
        when(gradeService.getByStudent(2L)).thenReturn(List.of(nota("90")));
        inscribir(2L, 10L);
        promedioSeccion(72.0);

        StudentSummaryResponse response = service.getSummary(2L);

        assertEquals(0, new BigDecimal("18.0").compareTo(response.difference()));
    }

    // Prueba 7: la diferencia es cero cuando el promedio del estudiante iguala al de la seccion
    @Test
    void getSummary_debeRetornarDiferenciaCero_cuandoPromediosSonIguales() {
        when(gradeService.getByStudent(3L)).thenReturn(List.of(nota("72"), nota("72")));
        inscribir(3L, 10L);
        promedioSeccion(72.0);

        StudentSummaryResponse response = service.getSummary(3L);

        assertEquals(0, BigDecimal.ZERO.compareTo(response.difference()));
    }

    // Prueba 8: un estudiante sin notas no tiene promedio y lanza StudentNotFoundException
    @Test
    void getSummary_debeLanzarExcepcion_cuandoEstudianteNoTieneNotas() {
        when(gradeService.getByStudent(99L)).thenReturn(List.of());

        StudentNotFoundException exception = assertThrows(
                StudentNotFoundException.class,
                () -> service.getSummary(99L)
        );

        assertEquals("No se encontró el estudiante con id: 99", exception.getMessage());
    }

    // Prueba 9: sin inscripcion se usa el promedio de respaldo de la seccion (72.0)
    @Test
    void getSummary_debeUsarFallback_cuandoEstudianteNoTieneInscripcion() {
        when(gradeService.getByStudent(4L)).thenReturn(List.of(nota("60")));

        StudentSummaryResponse response = service.getSummary(4L);

        assertEquals(0, new BigDecimal("72.0").compareTo(response.sectionAverage()));
        assertEquals(0, new BigDecimal("-12.0").compareTo(response.difference()));
    }

    // Prueba 10: si B1 falla se usa el promedio de respaldo de la seccion (72.0)
    @Test
    void getSummary_debeUsarFallback_cuandoB1Falla() {
        when(gradeService.getByStudent(5L)).thenReturn(List.of(nota("60")));
        inscribir(5L, 10L);
        when(centralTendencyService.getSectionCentralTendency(anyLong(), eq(null)))
                .thenThrow(new RuntimeException("B1 no disponible"));

        StudentSummaryResponse response = service.getSummary(5L);

        assertEquals(0, new BigDecimal("72.0").compareTo(response.sectionAverage()));
    }

    // Prueba 11: si B2 falla se usa el percentil de respaldo (20)
    @Test
    void getComparison_debeUsarPercentilDeRespaldo_cuandoB2Falla() {
        when(gradeService.getByStudent(6L)).thenReturn(List.of(nota("60")));
        inscribir(6L, 10L);
        promedioSeccion(72.0);
        when(positionService.getStudentPosition(6L))
                .thenThrow(new RuntimeException("B2 no disponible"));

        StudentComparisonResponse response = service.getComparison(6L);

        assertEquals(20, response.percentile());
    }

    // Prueba 12: sin datos en B4 (null) o si B4 falla, la tendencia es INSUFFICIENT_DATA
    @Test
    void getTrend_debeRetornarInsufficientData_cuandoB4NoTieneDatosOFalla() {
        when(trendService.getTrendByStudentId(8L)).thenReturn(null);
        when(trendService.getTrendByStudentId(9L)).thenThrow(new RuntimeException("B4 no disponible"));

        StudentTrendResponse sinDatos = service.getTrend(8L);
        StudentTrendResponse conFallo = service.getTrend(9L);

        assertEquals("INSUFFICIENT_DATA", sinDatos.trend());
        assertNull(sinDatos.averageChange());
        assertEquals("INSUFFICIENT_DATA", conFallo.trend());
    }
}
