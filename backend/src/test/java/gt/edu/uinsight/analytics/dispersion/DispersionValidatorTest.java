package gt.edu.uinsight.analytics.dispersion;

import gt.edu.uinsight.analytics.dispersion.entity.Grade;
import gt.edu.uinsight.analytics.dispersion.exception.DatosInsuficientesException;
import gt.edu.uinsight.analytics.dispersion.exception.DatosInvalidosException;
import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DispersionValidatorTest {

    private Grade crearGrade(BigDecimal score) {
        Grade grade = new Grade();
        grade.setScore(score);
        return grade;
    }

    @Test
    void listaNulaLanzaDatosInsuficientesException() {
        assertThrows(DatosInsuficientesException.class,
                () -> DispersionValidator.validar(null));
    }

    @Test
    void listaVaciaLanzaDatosInsuficientesException() {
        List<Grade> calificaciones = Collections.emptyList();
        assertThrows(DatosInsuficientesException.class,
                () -> DispersionValidator.validar(calificaciones));
    }

    @Test
    void listaConUnSoloRegistroLanzaDatosInsuficientesException() {
        List<Grade> calificaciones = List.of(crearGrade(new BigDecimal("85.00")));
        assertThrows(DatosInsuficientesException.class,
                () -> DispersionValidator.validar(calificaciones));
    }

    @Test
    void listaConScoreNuloLanzaDatosInvalidosException() {
        List<Grade> calificaciones = new ArrayList<>();
        calificaciones.add(crearGrade(new BigDecimal("70.00")));
        calificaciones.add(crearGrade(null));
        assertThrows(DatosInvalidosException.class,
                () -> DispersionValidator.validar(calificaciones));
    }

    @Test
    void listaValidaNoLanzaExcepciones() {
        List<Grade> calificaciones = List.of(
                crearGrade(new BigDecimal("60.00")),
                crearGrade(new BigDecimal("80.00")),
                crearGrade(new BigDecimal("95.00")));
        assertDoesNotThrow(() -> DispersionValidator.validar(calificaciones));
    }
}
