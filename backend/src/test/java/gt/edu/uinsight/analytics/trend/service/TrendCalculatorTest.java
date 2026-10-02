// Celula B4 - Evolucion y Tendencia
package gt.edu.uinsight.analytics.trend.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import gt.edu.uinsight.analytics.trend.service.TrendCalculator.Result;
import gt.edu.uinsight.analytics.trend.service.TrendCalculator.ScorePoint;

import static org.assertj.core.api.Assertions.assertThat;

class TrendCalculatorTest {

    private static final BigDecimal NEGATIVE_THRESHOLD = BigDecimal.valueOf(-3);
    private static final BigDecimal POSITIVE_THRESHOLD = BigDecimal.valueOf(3);

    private ScorePoint point(String label, double value) {
        return new ScorePoint(label, 1L, BigDecimal.valueOf(value));
    }

    @Test
    void calculate_debeRetornarInsufficientData_cuandoHayMenosDeDosPuntos() {
        Result result = TrendCalculator.calculate(List.of(point("E1", 78)), NEGATIVE_THRESHOLD, POSITIVE_THRESHOLD);

        assertThat(result.classification()).isEqualTo(TrendClassification.INSUFFICIENT_DATA);
        assertThat(result.averageChange()).isNull();
    }

    @Test
    void calculate_debeRetornarInsufficientData_cuandoLaListaEstaVacia() {
        Result result = TrendCalculator.calculate(List.of(), NEGATIVE_THRESHOLD, POSITIVE_THRESHOLD);

        assertThat(result.classification()).isEqualTo(TrendClassification.INSUFFICIENT_DATA);
    }

    @Test
    void calculate_debeClasificarComoNegative_conElEjemploDelDocumentoDelProyecto() {
        // E1=78, E2=72, E3=68 -> cambios -6 y -4 -> promedio -5 -> NEGATIVE
        List<ScorePoint> points = List.of(point("E1", 78), point("E2", 72), point("E3", 68));

        Result result = TrendCalculator.calculate(points, NEGATIVE_THRESHOLD, POSITIVE_THRESHOLD);

        assertThat(result.classification()).isEqualTo(TrendClassification.NEGATIVE);
        assertThat(result.averageChange()).isEqualByComparingTo("-5.00");
    }

    @Test
    void calculate_debeClasificarComoPositive_cuandoElPromedioSubeSobreElUmbral() {
        List<ScorePoint> points = List.of(point("E1", 60), point("E2", 68), point("E3", 76));

        Result result = TrendCalculator.calculate(points, NEGATIVE_THRESHOLD, POSITIVE_THRESHOLD);

        assertThat(result.classification()).isEqualTo(TrendClassification.POSITIVE);
        assertThat(result.averageChange()).isEqualByComparingTo("8.00");
    }

    @Test
    void calculate_debeClasificarComoStable_cuandoElCambioEstaDentroDelUmbral() {
        List<ScorePoint> points = List.of(point("E1", 70), point("E2", 71), point("E3", 70));

        Result result = TrendCalculator.calculate(points, NEGATIVE_THRESHOLD, POSITIVE_THRESHOLD);

        assertThat(result.classification()).isEqualTo(TrendClassification.STABLE);
    }

    @Test
    void calculate_debeUsarLosUmbralesRecibidos_noValoresFijosEnElCodigo() {
        // El mismo cambio (-2) es STABLE con umbral -3, pero NEGATIVE con umbral -1.
        List<ScorePoint> points = List.of(point("E1", 80), point("E2", 78));

        Result conUmbralAmplio = TrendCalculator.calculate(points, BigDecimal.valueOf(-3), BigDecimal.valueOf(3));
        Result conUmbralEstricto = TrendCalculator.calculate(points, BigDecimal.valueOf(-1), BigDecimal.valueOf(1));

        assertThat(conUmbralAmplio.classification()).isEqualTo(TrendClassification.STABLE);
        assertThat(conUmbralEstricto.classification()).isEqualTo(TrendClassification.NEGATIVE);
    }
}
