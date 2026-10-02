
package gt.edu.uinsight.analytics.dispersion.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class DispersionCalculator {

    private static final BigDecimal MIN_SCORE = BigDecimal.ZERO;
    private static final BigDecimal MAX_SCORE = new BigDecimal("100");

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    public BigDecimal calculateMin(List<BigDecimal> scores) {
        validateScores(scores);
        return Collections.min(scores);
    }

    public BigDecimal calculateMax(List<BigDecimal> scores) {
        validateScores(scores);
        return Collections.max(scores);
    }

    public BigDecimal calculateRange(List<BigDecimal> scores) {
        validateScores(scores);

        return calculateMax(scores)
                .subtract(calculateMin(scores), MC);
    }

    public BigDecimal calculateVariance(List<BigDecimal> scores) {
        validateScores(scores);

        if (scores.size() < 2) {
            throw new IllegalArgumentException(
                    "Se necesitan al menos dos calificaciones para calcular la varianza"
            );
        }

        int n = scores.size();

        BigDecimal sum = BigDecimal.ZERO;

        for (BigDecimal score : scores) {
            sum = sum.add(score, MC);
        }

        BigDecimal mean = sum.divide(
                BigDecimal.valueOf(n),
                MC
        );

        BigDecimal sumOfSquares = BigDecimal.ZERO;

        for (BigDecimal score : scores) {

            BigDecimal difference = score.subtract(mean, MC);

            BigDecimal squaredDifference =
                    difference.multiply(difference, MC);

            sumOfSquares = sumOfSquares.add(
                    squaredDifference,
                    MC
            );
        }

        // Varianza poblacional
        return sumOfSquares.divide(
                BigDecimal.valueOf(n),
                MC
        );
    }

    public BigDecimal calculateStandardDeviation(
            List<BigDecimal> scores) {

        BigDecimal variance = calculateVariance(scores);

        if (variance.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return variance.sqrt(MC);
    }

    private void validateScores(List<BigDecimal> scores) {

        if (scores == null || scores.isEmpty()) {
            throw new IllegalArgumentException(
                    "La lista de calificaciones no puede estar vacía o nula"
            );
        }

        for (BigDecimal score : scores) {

            if (score == null) {
                throw new IllegalArgumentException(
                        "Las calificaciones no pueden contener valores nulos"
                );
            }

            if (score.compareTo(MIN_SCORE) < 0 ||
                score.compareTo(MAX_SCORE) > 0) {

                throw new IllegalArgumentException(
                        "Las calificaciones deben estar entre 0 y 100"
                );
            }
        }
    }
}
