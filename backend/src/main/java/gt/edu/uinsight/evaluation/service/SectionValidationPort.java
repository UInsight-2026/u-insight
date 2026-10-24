// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.service;

/**
 * Puerto que aísla al módulo de evaluaciones de la forma concreta en que se
 * valida la existencia y el estado de una sección (dominio de la célula A4).
 *
 * Implementación actual: {@link SectionValidationJdbcAdapter} (temporal, ver
 * su javadoc). Cuando A4 exponga su API, se reemplaza por un cliente HTTP
 * real, sin tocar a los consumidores de este puerto (EvaluationServiceImpl).
 */
public interface SectionValidationPort {

    /** RN1: indica si existe una sección con el id dado. */
    boolean exists(Long sectionId);

    /**
     * RN1: indica si la sección está activa.
     * El resultado con una sección inexistente no está definido; llamar solo
     * después de confirmar {@link #exists(Long)}.
     */
    boolean isActive(Long sectionId);
}
