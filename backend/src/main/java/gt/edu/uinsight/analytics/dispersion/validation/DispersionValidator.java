package gt.edu.uinsight.analytics.dispersion.validation;

import java.math.BigDecimal;
import java.util.List;

import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInsuficientesException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;

/**
 * Validaciones relacionadas con las calificaciones del módulo de dispersión.
 *
 * Controla que existan suficientes calificaciones y que cada score
 * se encuentre dentro del rango permitido de 0 a 100.
 *
 * Responsable: Zarbya Yanina Hernandez Hernandez
 */
public final class DispersionValidator {

    private static final int MINIMO_DATOS_REQUERIDOS = 2;

    private static final BigDecimal MINIMO_SCORE =
            BigDecimal.ZERO;

    private static final BigDecimal MAXIMO_SCORE =
            new BigDecimal("100");

    private DispersionValidator() {
    }

    /**
     * Valida una lista de scores antes de realizar el cálculo
     * de dispersión.
     *
     * @param scores lista de calificaciones
     *
     * @throws DispersionDatosInsuficientesException
     *         si no existen suficientes calificaciones
     *
     * @throws DispersionDatosInvalidosException
     *         si existe un score nulo o fuera del rango 0-100
     */
    public static void validar(List<BigDecimal> scores) {

        if (scores == null || scores.isEmpty()) {
            throw new DispersionDatosInsuficientesException(
                    "No hay calificaciones registradas para calcular la dispersión."
            );
        }

        if (scores.size() < MINIMO_DATOS_REQUERIDOS) {
            throw new DispersionDatosInsuficientesException(
                    "Se requieren al menos "
                            + MINIMO_DATOS_REQUERIDOS
                            + " calificaciones para calcular la dispersión. "
                            + "Encontradas: "
                            + scores.size()
            );
        }

        for (BigDecimal score : scores) {

            if (score == null) {
                throw new DispersionDatosInvalidosException(
                        "Existe una calificación sin score registrado."
                );
            }

            if (score.compareTo(MINIMO_SCORE) < 0
                    || score.compareTo(MAXIMO_SCORE) > 0) {

                throw new DispersionDatosInvalidosException(
                        "Las calificaciones deben estar entre 0 y 100."
                );
            }
        }
    }
}
