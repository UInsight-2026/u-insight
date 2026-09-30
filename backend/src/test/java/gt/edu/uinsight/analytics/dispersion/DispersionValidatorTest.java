package gt.edu.uinsight.analytics.dispersion;

import gt.edu.uinsight.analytics.dispersion.entity.DispersionGrade;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInsuficientesException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;
import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DispersionValidatorTest {

    private DispersionGrade crearGrade(BigDecimal score) {
        DispersionGrade grade = new DispersionGrade();
        grade.setScore(score);
        return grade;
    }

    @Test
    void listaNulaLanzaDispersionDatosInsuficientesException() {
        assertThrows(DispersionDatosInsuficientesException.class,
                () -> DispersionValidator.validar(null));
    }

    @Test
    void listaVaciaLanzaDispersionDatosInsuficientesException() {
        List<DispersionGrade> calificaciones = Collections.emptyList();
        assertThrows(DispersionDatosInsuficientesException.class,
                () -> DispersionValidator.validar(calificaciones));
    }

    @Test
    void listaConUnSoloRegistroLanzaDispersionDatosInsuficientesException() {
        List<DispersionGrade> calificaciones =
                List.of(crearGrade(new BigDecimal("85.00")));

        assertThrows(DispersionDatosInsuficientesException.class,
                () -> DispersionValidator.validar(calificaciones));
    }

    @Test
    void listaConScoreNuloLanzaDispersionDatosInvalidosException() {
        List<DispersionGrade> calificaciones = new ArrayList<>();
        calificaciones.add(crearGrade(new BigDecimal("70.00")));
        calificaciones.add(crearGrade(null));

        assertThrows(DispersionDatosInvalidosException.class,
                () -> DispersionValidator.validar(calificaciones));
    }

    @Test
    void listaValidaNoLanzaExcepciones() {
        List<DispersionGrade> calificaciones = List.of(
                crearGrade(new BigDecimal("60.00")),
                crearGrade(new BigDecimal("80.00")),
                crearGrade(new BigDecimal("95.00")));

        assertDoesNotThrow(() -> DispersionValidator.validar(calificaciones));
    }
}
