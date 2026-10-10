// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.api;

import java.util.List;
import java.util.Optional;

/**
 * Contrato público de solo lectura del módulo de evaluaciones (A5) para
 * otras células del monolito. Pensado para A6 (calificaciones): antes de
 * registrar una nota puede validar que la evaluación exista, esté ACTIVE y
 * que la nota no supere maximumScore, sin depender de la entidad JPA de A5.
 */
public interface EvaluationQueryService {

    /** Resumen de la evaluación, o vacío si no existe. */
    Optional<EvaluationSummary> findSummary(Long evaluationId);

    /** true si la evaluación existe y está ACTIVE. */
    boolean isGradable(Long evaluationId);

    /** Evaluaciones de una sección (cualquier estado). */
    List<EvaluationSummary> findBySection(Long sectionId);
}
