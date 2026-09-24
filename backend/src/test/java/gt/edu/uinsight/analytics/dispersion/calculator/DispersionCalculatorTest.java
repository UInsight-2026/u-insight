package gt.edu.uinsight.analytics.dispersion.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DispersionCalculatorTest {

    private DispersionCalculator calculator;
    private List<BigDecimal> sampleScores;

    @BeforeEach
    public void setUp() {
        calculator = new DispersionCalculator();
        sampleScores = Arrays.asList(
            new BigDecimal("70.00"),
            new BigDecimal("80.00"),
            new BigDecimal("90.00")
        );
    }

    @Test
    public void testCalculateMin() {
        BigDecimal min = calculator.calculateMin(sampleScores);
        assertEquals(0, min.compareTo(new BigDecimal("70.00")));
    }

    @Test
    public void testCalculateMax() {
        BigDecimal max = calculator.calculateMax(sampleScores);
        assertEquals(0, max.compareTo(new BigDecimal("90.00")));
    }

    @Test
    public void testCalculateRange() {
        BigDecimal range = calculator.calculateRange(sampleScores);
        assertEquals(0, range.compareTo(new BigDecimal("20.00")));
    }

    @Test
    public void testCalculateVariance() {
        BigDecimal variance = calculator.calculateVariance(sampleScores);
        assertEquals(0, variance.compareTo(new BigDecimal("100.00")));
    }

    @Test
    public void testCalculateStandardDeviation() {
        BigDecimal stdDev = calculator.calculateStandardDeviation(sampleScores);
        assertEquals(0, stdDev.compareTo(new BigDecimal("10.00")));
    }

    @Test
    public void testCalculateWithEmptyListShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateMin(Arrays.asList());
        });
    }
}
