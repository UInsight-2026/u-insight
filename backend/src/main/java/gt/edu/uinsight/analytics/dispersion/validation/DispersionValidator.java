package gt.edu.uinsight.analytics.dispersion.validation;

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

    private DispersionValidator() {
    }

    /**
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
            }
        }
    }
}
