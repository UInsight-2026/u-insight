package gt.edu.uinsight.analytics.dispersion.validation;

import gt.edu.uinsight.analytics.dispersion.entity.Grade;
import gt.edu.uinsight.analytics.dispersion.exception.DatosInsuficientesException;
import gt.edu.uinsight.analytics.dispersion.exception.DatosInvalidosException;

import java.util.List;

/**
 * Validaciones relacionadas con el módulo de dispersión: controla
 * ausencia o insuficiencia de calificaciones (Grade) antes de que
 * el calculator/service opere sobre la lista obtenida de GradeRepository.
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
     * @param calificaciones lista de Grade obtenida del repositorio
     * @throws DatosInsuficientesException si la lista es nula, vacía o tiene menos del mínimo requerido
     * @throws DatosInvalidosException     si algún registro tiene score nulo
     */
    public static void validar(List<Grade> calificaciones) {
        if (calificaciones == null || calificaciones.isEmpty()) {
            throw new DatosInsuficientesException(
                    "No hay calificaciones registradas para calcular la dispersión.");
        }

        if (calificaciones.size() < MINIMO_DATOS_REQUERIDOS) {
            throw new DatosInsuficientesException(
                    "Se requieren al menos " + MINIMO_DATOS_REQUERIDOS
                            + " calificaciones para calcular la dispersión. Encontradas: "
                            + calificaciones.size());
        }

        for (Grade calificacion : calificaciones) {
            if (calificacion == null || calificacion.getScore() == null) {
                throw new DatosInvalidosException(
                        "Existe una calificación sin score registrado.");
            }
        }
    }
}
