// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.api.EvaluationSummary;

/**
 * Integración A5 -> A6 (entregable semana 4).
 *
 * A6 mantiene su propia tabla de referencia de evaluaciones
 * (grade_evaluation_ref) para validar el registro de notas. En vez de cargarla
 * a mano, A5 publica ahí cada evaluación cuando pasa a ACTIVE o cuando se
 * modifica estando ACTIVE, de modo que A6 siempre valida contra datos reales.
 */
public interface GradeEvaluationSyncPort {

    /** Crea o actualiza la referencia de la evaluación en el módulo de calificaciones. */
    void publish(EvaluationSummary evaluation);
}
