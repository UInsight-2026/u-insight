package gt.edu.uinsight.analytics.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.trend.entity.Evaluation;

// Parche de arranque aportado por C7: la celula A5 declara otra interfaz llamada
// EvaluationRepository. Ambas peleaban por el nombre de bean 'evaluationRepository' y el
// contexto de Spring no arrancaba (BeanDefinitionOverrideException). Se nombra
// explicitamente este bean; la inyeccion por tipo no cambia.
@Repository("trendEvaluationRepository")
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
