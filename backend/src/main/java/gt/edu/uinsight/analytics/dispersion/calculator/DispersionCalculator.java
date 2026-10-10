
package gt.edu.uinsight.analytics.dispersion.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;

@Component
public class DispersionCalculator {

    private static final MathContext MC =
            new MathContext(12, RoundingMode.HALF_UP);

    public BigDecimal calculateMin(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);
        return Collections.min(scores);
    }

    public BigDecimal calculateMax(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);
        return Collections.max(scores);
    }

    public BigDecimal calculateRange(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);

        BigDecimal min = Collections.min(scores);
        BigDecimal max = Collections.max(scores);

        return max.subtract(min, MC);
    }

    public BigDecimal calculateVariance(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);

        int n = scores.size();
        BigDecimal sum = BigDecimal.ZERO;

        for (BigDecimal score : scores) {
            sum = sum.add(score, MC);
        }

        BigDecimal mean = sum.divide(
                BigDecimal.valueOf(n), MC
        );

        BigDecimal sumOfSquares = BigDecimal.ZERO;

        for (BigDecimal score : scores) {
            BigDecimal difference = score.subtract(mean, MC);
            BigDecimal squaredDifference =
                    difference.multiply(difference, MC);

            sumOfSquares = sumOfSquares.add(
                    squaredDifference, MC
            );
        }

        // Varianza poblacional.
        return sumOfSquares.divide(
                BigDecimal.valueOf(n), MC
        );
    }

    public BigDecimal calculateStandardDeviation(
            List<BigDecimal> scores) {

        BigDecimal variance = calculateVariance(scores);

        if (variance.signum() == 0) {
            return BigDecimal.ZERO;
        }

        return variance.sqrt(MC);
    }
}
