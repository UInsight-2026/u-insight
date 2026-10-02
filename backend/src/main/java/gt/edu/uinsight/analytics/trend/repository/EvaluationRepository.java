package gt.edu.uinsight.analytics.trend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.trend.entity.Evaluation;

// Parche de arranque aportado por C7: la celula A5 declara otra interfaz llamada
// EvaluationRepository. Ambas peleaban por el nombre de bean 'evaluationRepository' y el
// contexto de Spring no arrancaba (BeanDefinitionOverrideException). Se nombra
// explicitamente este bean; la inyeccion por tipo no cambia.
@Repository("trendEvaluationRepository")
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query (value = "SELECT e.* FROM evaluation e WHERE e.id = :evaluationId ORDER BY e.evaluation_date ASC", nativeQuery = true)
    Evaluation findByEvaluationId(@Param("evaluationId") Long evaluationId);
}
