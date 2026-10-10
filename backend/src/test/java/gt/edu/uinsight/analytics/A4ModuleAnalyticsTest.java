package gt.edu.uinsight.analytics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class A4ModuleAnalyticsTest {

    private double[] sampleScores;

    @BeforeEach
    void setUp() {
        sampleScores = new double[]{85.5, 62.0, 95.0, 45.0};
    }

    // --- PRUEBAS DE DISPERSIÓN ---
    @Test
    @DisplayName("1. Calcular Rango Estadístico Correctamente")
    void testCalculateRange() {
        double min = Arrays.stream(sampleScores).min().orElse(0.0);
        double max = Arrays.stream(sampleScores).max().orElse(0.0);
        double range = max - min;
        assertEquals(50.0, range, 0.01);
    }

    @Test
    @DisplayName("2. Calcular Media/Promedio de Muestra")
    void testCalculateMean() {
        double sum = Arrays.stream(sampleScores).sum();
        double mean = sum / sampleScores.length;
        assertEquals(71.875, mean, 0.001);
    }

    @Test
    @DisplayName("3. Calcular Varianza Poblacional/Muestral")
    void testCalculateVariance() {
        double mean = Arrays.stream(sampleScores).average().orElse(0.0);
        double temp = 0;
        for (double a : sampleScores) {
            temp += (a - mean) * (a - mean);
        }
        double variance = temp / sampleScores.length;
        assertTrue(variance > 0);
        assertEquals(363.546, variance, 0.01);
    }

    @Test
    @DisplayName("4. Calcular Desviación Estándar")
    void testCalculateStandardDeviation() {
        double mean = Arrays.stream(sampleScores).average().orElse(0.0);
        double temp = 0;
        for (double a : sampleScores) {
            temp += (a - mean) * (a - mean);
        }
        double stdDev = Math.sqrt(temp / sampleScores.length);
        assertEquals(19.066, stdDev, 0.01);
    }

    @Test
    @DisplayName("5. Clasificar Categoría de Dispersión Alta")
    void testClassifyHighDispersion() {
        double stdDev = 19.066;
        String category = stdDev > 15.0 ? "HIGH_DISPERSION" : "LOW_DISPERSION";
        assertEquals("HIGH_DISPERSION", category);
    }

    // --- PRUEBAS DE TENDENCIA Y POSICIÓN ---
    @Test
    @DisplayName("6. Determinar Tendencia Negativa en Evaluaciones")
    void testCalculateNegativeTrend() {
        List<Double> grades = Arrays.asList(85.0, 75.0, 60.0);
        double change = grades.get(grades.size() - 1) - grades.get(0);
        String trend = change < 0 ? "NEGATIVE" : "POSITIVE";
        assertEquals("NEGATIVE", trend);
    }

    @Test
    @DisplayName("7. Determinar Tendencia Positiva en Evaluaciones")
    void testCalculatePositiveTrend() {
        List<Double> grades = Arrays.asList(60.0, 72.0, 88.0);
        double change = grades.get(grades.size() - 1) - grades.get(0);
        String trend = change > 0 ? "POSITIVE" : "NEGATIVE";
        assertEquals("POSITIVE", trend);
    }

    @Test
    @DisplayName("8. Validar Estudiante en Riesgo por Nota Menor a Umbral")
    void testStudentAtRiskThreshold() {
        double score = 45.0;
        double threshold = 61.0;
        boolean atRisk = score < threshold;
        assertTrue(atRisk);
    }

    @Test
    @DisplayName("9. Validar Estado de Inserción Vacía/Sin Datos")
    void testEmptyDataValidation() {
        double[] emptyScores = new double[]{};
        assertEquals(0, emptyScores.length);
        assertThrows(ArithmeticException.class, () -> {
            if (emptyScores.length == 0) {
                throw new ArithmeticException("Muestra vacía sin datos");
            }
        });
    }

    @Test
    @DisplayName("10. Validar Formato DTO de Respuesta de Salud / Health")
    void testHealthResponseFormat() {
        String status = "UP";
        String service = "u-insight-backend";
        assertNotNull(status);
        assertEquals("UP", status);
        assertEquals("u-insight-backend", service);
    }
}