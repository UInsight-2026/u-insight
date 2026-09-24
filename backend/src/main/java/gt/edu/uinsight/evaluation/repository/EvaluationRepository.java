package gt.edu.uinsight.evaluation.repository;

import gt.edu.uinsight.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}