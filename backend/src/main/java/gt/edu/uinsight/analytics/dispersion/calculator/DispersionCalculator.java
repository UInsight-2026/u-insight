package gt.edu.uinsight.analytics.dispersion.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;

import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;

@Component
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
        return calculateMax(scores).subtract(calculateMin(scores));
    }

    public BigDecimal calculateVariance(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);

        BigDecimal sum = scores.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal mean = sum.divide(
                BigDecimal.valueOf(scores.size()), MC);

        BigDecimal squaredDifferences = BigDecimal.ZERO;

        for (BigDecimal score : scores) {
            BigDecimal difference = score.subtract(mean);
            squaredDifferences = squaredDifferences.add(
                    difference.multiply(difference, MC));
        }

        return squaredDifferences.divide(
                BigDecimal.valueOf(scores.size()), MC);
    }

    public BigDecimal calculateStandardDeviation(
            List<BigDecimal> scores) {

        BigDecimal variance = calculateVariance(scores);

        return BigDecimal.valueOf(
                Math.sqrt(variance.doubleValue()));
    }
}
