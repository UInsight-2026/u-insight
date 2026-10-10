package gt.edu.uinsight.analytics.dispersion.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;

public class DispersionCalculator {

    private static final MathContext MC =
            new MathContext(16, RoundingMode.HALF_UP);

    public BigDecimal calculateMin(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);

        return scores.stream()
                .min(BigDecimal::compareTo)
                .orElseThrow();
    }

    public BigDecimal calculateMax(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);

        return scores.stream()
                .max(BigDecimal::compareTo)
                .orElseThrow();
    }

    public BigDecimal calculateRange(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);

        return calculateMax(scores).subtract(calculateMin(scores));
    }

    public BigDecimal calculateVariance(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);

        BigDecimal mean = calculateMean(scores);
        BigDecimal sumOfSquaredDifferences = BigDecimal.ZERO;

        for (BigDecimal score : scores) {
            BigDecimal difference = score.subtract(mean);
            BigDecimal squaredDifference =
                    difference.multiply(difference);

            sumOfSquaredDifferences =
                    sumOfSquaredDifferences.add(squaredDifference);
        }

        return sumOfSquaredDifferences.divide(
                BigDecimal.valueOf(scores.size()), MC);
    }

    public BigDecimal calculateStandardDeviation(
            List<BigDecimal> scores) {

        DispersionValidator.validar(scores);

        return calculateVariance(scores).sqrt(MC);
    }

    private BigDecimal calculateMean(List<BigDecimal> scores) {
        BigDecimal sum = BigDecimal.ZERO;

        for (BigDecimal score : scores) {
            sum = sum.add(score);
        }

        return sum.divide(BigDecimal.valueOf(scores.size()), MC);
    }

    private void validateScores(List<BigDecimal> scores) {
        if (scores == null || scores.isEmpty()) {
            throw new IllegalArgumentException("La lista de calificaciones no puede estar vacía o nula");
        }
    }
}
