package gt.edu.uinsight.analytics.dispersion.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class DispersionCalculator {

    private static final MathContext MC = new MathContext(6, RoundingMode.HALF_UP);

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
        return calculateMax(scores).subtract(calculateMin(scores), MC);
    }

    public BigDecimal calculateVariance(List<BigDecimal> scores) {
        validateScores(scores);
        int n = scores.size();
        if (n < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal score : scores) {
            sum = sum.add(score);
        }
        BigDecimal mean = sum.divide(BigDecimal.valueOf(n), MC);

        BigDecimal sumOfSquares = BigDecimal.ZERO;
        for (BigDecimal score : scores) {
            BigDecimal diff = score.subtract(mean);
            sumOfSquares = sumOfSquares.add(diff.multiply(diff));
        }

        
        return sumOfSquares.divide(BigDecimal.valueOf(n - 1), MC);
    }

    public BigDecimal calculateStandardDeviation(List<BigDecimal> scores) {
        validateScores(scores);
        BigDecimal variance = calculateVariance(scores);
        if (variance.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return variance.sqrt(MC);
    }

    private void validateScores(List<BigDecimal> scores) {
        if (scores == null || scores.isEmpty()) {
            throw new IllegalArgumentException("La lista de calificaciones no puede estar vacía o nula");
        }
    }
}
