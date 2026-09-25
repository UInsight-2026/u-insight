package gt.edu.uinsight.analytics.centraltendency.calculator;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CentralTendencyCalculatorTest {

    private CentralTendencyCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new CentralTendencyCalculator();
    }

    @Test
    void returnsEmptyResultWhenThereAreNoScores() {
        CentralTendencyResponse response = calculator.calculate(List.of());

        assertEquals(0, response.sampleSize());
        assertNull(response.mean());
        assertNull(response.median());
        assertEquals(List.of(), response.mode());
    }

    @Test
    void returnsEmptyResultWhenScoreListIsNull() {
        CentralTendencyResponse response = calculator.calculate(null);

        assertEquals(0, response.sampleSize());
        assertNull(response.mean());
        assertNull(response.median());
        assertEquals(List.of(), response.mode());
    }

    @Test
    void ignoresNullScoresBeforeCalculating() {
        CentralTendencyResponse response = calculator.calculate(
                Arrays.asList(new BigDecimal("80"), null, new BigDecimal("90"))
        );

        assertEquals(2, response.sampleSize());
        assertEquals(85.0, response.mean());
        assertEquals(85.0, response.median());
        assertEquals(List.of(), response.mode());
    }

    @Test
    void usesTheOnlyScoreAsMeanMedianAndMode() {
        CentralTendencyResponse response = calculator.calculate(List.of(new BigDecimal("85")));

        assertEquals(1, response.sampleSize());
        assertEquals(85.0, response.mean());
        assertEquals(85.0, response.median());
        assertEquals(List.of(85.0), response.mode());
    }

    @Test
    void calculatesMedianForAnEvenNumberOfScores() {
        CentralTendencyResponse response = calculator.calculate(scores("60", "70", "80", "90"));

        assertEquals(75.0, response.mean());
        assertEquals(75.0, response.median());
        assertEquals(List.of(), response.mode());
    }

    @Test
    void calculatesMedianForAnOddNumberOfUnsortedScores() {
        CentralTendencyResponse response = calculator.calculate(scores("90", "70", "80"));

        assertEquals(80.0, response.mean());
        assertEquals(80.0, response.median());
        assertEquals(List.of(), response.mode());
    }

    @Test
    void returnsNoModeWhenEveryScoreHasFrequencyOne() {
        CentralTendencyResponse response = calculator.calculate(scores("65", "70", "80"));

        assertEquals(List.of(), response.mode());
    }

    @Test
    void returnsAllModesInAscendingOrder() {
        CentralTendencyResponse response = calculator.calculate(
                scores("80", "70", "90", "80", "70")
        );

        assertEquals(List.of(70.0, 80.0), response.mode());
    }

    @Test
    void treatsEquivalentDecimalScalesAsTheSameMode() {
        CentralTendencyResponse response = calculator.calculate(
                scores("70.0", "70.00", "80.0")
        );

        assertEquals(List.of(70.0), response.mode());
    }

    @Test
    void roundsTheMeanToTwoDecimalPlaces() {
        CentralTendencyResponse response = calculator.calculate(scores("70", "70", "71"));

        assertEquals(70.33, response.mean());
    }

    private List<BigDecimal> scores(String... values) {
        return java.util.Arrays.stream(values).map(BigDecimal::new).toList();
    }
}
