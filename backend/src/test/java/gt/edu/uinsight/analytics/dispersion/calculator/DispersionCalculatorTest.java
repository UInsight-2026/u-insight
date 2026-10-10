package gt.edu.uinsight.analytics.dispersion.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInsuficientesException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;

class DispersionCalculatorTest {

    private DispersionCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DispersionCalculator();
    }

    private List<BigDecimal> scores(String... values) {
        return Arrays.stream(values)
                .map(BigDecimal::new)
                .toList();
    }

    @Test
void calculaMinimoCorrectamente() {
    assertEquals(
            0,
            calculator.calculateMin(scores("60", "80", "70"))
                    .compareTo(new BigDecimal("60")));
}

    @Test
    void calculaMaximoCorrectamente() {
        assertEquals(
                0,
                calculator.calculateMax(scores("60", "80", "70"))
                        .compareTo(new BigDecimal("80")));
    }

    @Test
    void calculaRangoCorrectamente() {
        assertEquals(
                0,
                calculator.calculateRange(scores("60", "80", "70"))
                        .compareTo(new BigDecimal("20")));
    }

    @Test
    void calculaVarianzaPoblacionalCorrectamente() {
        assertEquals(
                0,
                calculator.calculateVariance(scores("60", "80"))
                        .compareTo(new BigDecimal("100")));
    }

    @Test
    void calculaDesviacionEstandarCorrectamente() {
        BigDecimal result =
                calculator.calculateStandardDeviation(scores("60", "80"));

        assertEquals(
                0,
                result.compareTo(new BigDecimal("10")));
    }

    @Test
    void aceptaCalificacionesIguales() {
        assertEquals(
                0,
                calculator.calculateRange(scores("75", "75"))
                        .compareTo(BigDecimal.ZERO));
    }

    @Test
    void aceptaCalificacionMinimaCero() {
        assertEquals(
                0,
                calculator.calculateMin(scores("0", "100"))
                        .compareTo(BigDecimal.ZERO));
    }

    @Test
    void aceptaCalificacionMaximaCien() {
        assertEquals(
                0,
                calculator.calculateMax(scores("0", "100"))
                        .compareTo(new BigDecimal("100")));
    }

    @Test
    void rechazaListaNula() {
        assertThrows(
                DispersionDatosInsuficientesException.class,
                () -> calculator.calculateMin(null));
    }

    @Test
    void rechazaListaVacia() {
        assertThrows(
                DispersionDatosInsuficientesException.class,
                () -> calculator.calculateMax(List.of()));
    }

    @Test
    void rechazaUnaSolaCalificacion() {
        assertThrows(
                DispersionDatosInsuficientesException.class,
                () -> calculator.calculateRange(scores("80")));
    }

    @Test
    void rechazaCalificacionNula() {
        List<BigDecimal> invalidScores =
                Arrays.asList(new BigDecimal("70"), null);

        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> calculator.calculateVariance(invalidScores));
    }

    @Test
    void rechazaCalificacionNegativa() {
        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> calculator.calculateMin(scores("-1", "70")));
    }

    @Test
    void rechazaCalificacionMayorQueCien() {
        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> calculator.calculateMax(scores("70", "101")));
    }
}