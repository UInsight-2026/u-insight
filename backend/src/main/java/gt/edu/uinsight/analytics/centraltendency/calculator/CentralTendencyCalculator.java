package gt.edu.uinsight.analytics.centraltendency.calculator;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Realiza los cálculos de media, mediana y moda para la célula B1.
 */
@Component
public class CentralTendencyCalculator {

    private static final int SCALE = 2;

    /**
     * Calcula la media, mediana y moda de las calificaciones recibidas.
     *
     * Los valores null son ignorados y las calificaciones se ordenan
     * antes de calcular la mediana.
     */
    public CentralTendencyResponse calculate(List<BigDecimal> receivedScores) {

        List<BigDecimal> scores = receivedScores == null
                ? List.of()
                : receivedScores.stream()
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();

        if (scores.isEmpty()) {
            return CentralTendencyResponse.empty();
        }

        return new CentralTendencyResponse(
                scores.size(),
                calculateMean(scores),
                calculateMedian(scores),
                calculateMode(scores)
        );
    }

    /**
     * Calcula la media y la redondea a dos decimales.
     */
    private double calculateMean(List<BigDecimal> sortedScores) {

        BigDecimal sum = sortedScores.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(
                        BigDecimal.valueOf(sortedScores.size()),
                        SCALE,
                        RoundingMode.HALF_UP
                )
                .doubleValue();
    }

    /**
     * Calcula la mediana de una lista previamente ordenada.
     */
    private double calculateMedian(List<BigDecimal> sortedScores) {

        int size = sortedScores.size();
        int middle = size / 2;

        if (size % 2 != 0) {
            return sortedScores.get(middle)
                    .setScale(SCALE, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return sortedScores.get(middle - 1)
                .add(sortedScores.get(middle))
                .divide(
                        BigDecimal.valueOf(2),
                        SCALE,
                        RoundingMode.HALF_UP
                )
                .doubleValue();
    }

    /**
     * Calcula una o varias modas.
     *
     * stripTrailingZeros permite considerar valores como
     * 70.0 y 70.00 como la misma calificación.
     */
    private List<Double> calculateMode(List<BigDecimal> sortedScores) {

        if (sortedScores.size() == 1) {
            return List.of(toDouble(sortedScores.get(0)));
        }

        Map<BigDecimal, Long> frequencies = sortedScores.stream()
                .map(BigDecimal::stripTrailingZeros)
                .collect(Collectors.groupingBy(
                        value -> value,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        long maximumFrequency = frequencies.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        if (maximumFrequency <= 1) {
            return List.of();
        }

        return frequencies.entrySet().stream()
                .filter(entry -> entry.getValue() == maximumFrequency)
                .map(Map.Entry::getKey)
                .sorted()
                .map(this::toDouble)
                .toList();
    }

    /**
     * Convierte un BigDecimal a double utilizando dos decimales.
     */
    private double toDouble(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
