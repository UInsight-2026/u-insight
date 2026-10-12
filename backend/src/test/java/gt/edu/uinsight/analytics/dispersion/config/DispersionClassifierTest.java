package gt.edu.uinsight.analytics.dispersion.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;

class DispersionClassifierTest {

    private DispersionThresholdsProperties thresholds;
    private DispersionClassifier classifier;

    @BeforeEach
    void setUp() {
        thresholds = new DispersionThresholdsProperties();
        classifier = new DispersionClassifier(thresholds);
    }

    @Test
    void clasificaComoBajaCuandoEsMenorQueDiez() {
        assertEquals(
                DispersionClassification.LOW_DISPERSION,
                classifier.clasificar(new BigDecimal("5")));
    }

    @Test
    void clasificaCeroComoBaja() {
        assertEquals(
                DispersionClassification.LOW_DISPERSION,
                classifier.clasificar(BigDecimal.ZERO));
    }

    @Test
    void clasificaComoModeradaCuandoAlcanzaUmbralBajo() {
        assertEquals(
                DispersionClassification.MODERATE_DISPERSION,
                classifier.clasificar(new BigDecimal("10")));
    }

    @Test
    void clasificaComoModeradaEntreLosUmbrales() {
        assertEquals(
                DispersionClassification.MODERATE_DISPERSION,
                classifier.clasificar(new BigDecimal("20")));
    }

    @Test
    void clasificaComoAltaCuandoAlcanzaUmbralAlto() {
        assertEquals(
                DispersionClassification.HIGH_DISPERSION,
                classifier.clasificar(new BigDecimal("30")));
    }

    @Test
    void clasificaComoAltaCuandoSuperaUmbralAlto() {
        assertEquals(
                DispersionClassification.HIGH_DISPERSION,
                classifier.clasificar(new BigDecimal("45")));
    }

    @Test
    void rechazaDesviacionNula() {
        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> classifier.clasificar(null));
    }

    @Test
    void rechazaDesviacionNegativa() {
        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> classifier.clasificar(new BigDecimal("-1")));
    }

    @Test
    void rechazaUmbralesIguales() {
        thresholds.setLow(10);
        thresholds.setHigh(10);

        assertThrows(
                IllegalStateException.class,
                () -> classifier.clasificar(new BigDecimal("15")));
    }

    @Test
    void rechazaUmbralBajoNegativo() {
        thresholds.setLow(-1);
        thresholds.setHigh(30);

        assertThrows(
                IllegalStateException.class,
                () -> classifier.clasificar(new BigDecimal("15")));
    }

    @Test
    void rechazaUmbralAltoMenorQueUmbralBajo() {
        thresholds.setLow(30);
        thresholds.setHigh(10);

        assertThrows(
                IllegalStateException.class,
                () -> classifier.clasificar(new BigDecimal("15")));
    }

    @Test
    void rechazaUmbralAltoInfinito() {
        thresholds.setLow(10);
        thresholds.setHigh(Double.POSITIVE_INFINITY);

        assertThrows(
                IllegalStateException.class,
                () -> classifier.clasificar(new BigDecimal("15")));
    }
}