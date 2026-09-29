// Celula B5 - Analisis Individual | Pruebas unitarias de StudentAnalyticsService
package gt.edu.uinsight.analytics.individual.service;

import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentTrendResponse;
import gt.edu.uinsight.analytics.individual.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentAnalyticsServiceTest {

    private StudentAnalyticsService service;

    @BeforeEach
    void setUp() {
        service = new StudentAnalyticsService();
    }

    // Prueba 1: getSummary con un id valido devuelve el resumen correcto
    @Test
    void getSummary_debeRetornarResumen_cuandoIdEsValido() {
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
        StudentComparisonResponse response = service.getComparison(25L);

        assertNotNull(response);
        assertEquals("EST-0025", response.studentCode());
        assertEquals(0, new BigDecimal("-14.0").compareTo(response.difference()));
        assertEquals(20, response.percentile());
    }

    // Prueba 3: getTrend con un id valido devuelve la tendencia
    @Test
    void getTrend_debeRetornarTendencia_cuandoIdEsValido() {
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
}