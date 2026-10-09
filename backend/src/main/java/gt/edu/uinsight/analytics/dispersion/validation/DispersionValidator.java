package gt.edu.uinsight.analytics.dispersion.validation;

<<<<<<< HEAD
import gt.edu.uinsight.analytics.dispersion.entity.DispersionGrade;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInsuficientesException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;

import java.util.List;

/**
 * Validaciones relacionadas con el módulo de dispersión: controla
 * ausencia o insuficiencia de calificaciones (DispersionGrade) antes de que
 * el calculator/service opere sobre la lista obtenida de DispersionGradeRepository.
 *
 * Se usa dentro de DispersionService, antes de invocar al calculator,
 * para las dos rutas (por sección y por curso).
 *
 * Responsable: Zarbya Yanina Hernandez Hernandez
 */
public class DispersionValidator {

    private static final int MINIMO_DATOS_REQUERIDOS = 2;

=======
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

>>>>>>> develop
    private DispersionValidator() {
    }

    /**
<<<<<<< HEAD
     * Valida que la lista de calificaciones exista, no esté vacía,
     * tenga al menos dos elementos y que cada registro tenga un
     * score válido (no nulo).
     *
     * @param calificaciones lista de DispersionGrade obtenida del repositorio
     * @throws DispersionDatosInsuficientesException si la lista es nula, vacía
     *         o tiene menos del mínimo requerido
     * @throws DispersionDatosInvalidosException si algún registro tiene score nulo
     */
    public static void validar(List<DispersionGrade> calificaciones) {
        if (calificaciones == null || calificaciones.isEmpty()) {
            throw new DispersionDatosInsuficientesException(
                    "No hay calificaciones registradas para calcular la dispersión.");
        }

        if (calificaciones.size() < MINIMO_DATOS_REQUERIDOS) {
            throw new DispersionDatosInsuficientesException(
                    "Se requieren al menos " + MINIMO_DATOS_REQUERIDOS
                            + " calificaciones para calcular la dispersión. Encontradas: "
                            + calificaciones.size());
        }

        for (DispersionGrade calificacion : calificaciones) {
            if (calificacion == null || calificacion.getScore() == null) {
                throw new DispersionDatosInvalidosException(
                        "Existe una calificación sin score registrado.");
=======
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
>>>>>>> develop
            }
        }
    }
}
