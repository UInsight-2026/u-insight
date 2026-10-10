package gt.edu.uinsight.imports.repository;

import gt.edu.uinsight.grade.model.EvaluationRef;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Acceso de solo lectura a grade_evaluation_ref para resolver el evaluationCode
 * del CSV contra las evaluaciones reales.
 */
public interface EvaluacionReferenciaRepository extends Repository<EvaluationRef, Long> {

    List<EvaluationRef> findByNameIgnoreCase(String name);
}