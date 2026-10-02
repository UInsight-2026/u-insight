package gt.edu.uinsight.analytics.trend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Reglas de negocio implementadas aquí:
 *  - Regla 1: menos de 2 puntos -> INSUFFICIENT_DATA, sin averageChange.
 *  - Regla 4: la clasificación depende de umbrales configurables,
 *    nunca de valores fijos en el código.
 */
public final class TrendCalculator {

    private TrendCalculator() {
        // Clase de utilidades, no se instancia.
    }

    /** Un punto ya normalizado (escala 0-100) listo para comparar. */
    public record ScorePoint(String label, Long evaluationId, BigDecimal normalizedValue) {
    }

    /** Resultado del cálculo: clasificación + cambio promedio. */
    public record Result(TrendClassification classification, BigDecimal averageChange) {
    }

    public static Result calculate(List<ScorePoint> orderedPoints,
                                    BigDecimal negativeThreshold,
                                    BigDecimal positiveThreshold) {
        if (orderedPoints == null || orderedPoints.size() < 2) {
            return new Result(TrendClassification.INSUFFICIENT_DATA, null);
        }

        BigDecimal totalChange = BigDecimal.ZERO;
        int changeCount = 0;
        for (int i = 1; i < orderedPoints.size(); i++) {
            BigDecimal previous = orderedPoints.get(i - 1).normalizedValue();
            BigDecimal current = orderedPoints.get(i).normalizedValue();
            totalChange = totalChange.add(current.subtract(previous));
            changeCount++;
        }

        BigDecimal averageChange = totalChange.divide(
                BigDecimal.valueOf(changeCount), 2, RoundingMode.HALF_UP);

        TrendClassification classification;
        if (averageChange.compareTo(negativeThreshold) <= 0) {
            classification = TrendClassification.NEGATIVE;
        } else if (averageChange.compareTo(positiveThreshold) >= 0) {
            classification = TrendClassification.POSITIVE;
        } else {
            classification = TrendClassification.STABLE;
        }

        return new Result(classification, averageChange);
    }
}
