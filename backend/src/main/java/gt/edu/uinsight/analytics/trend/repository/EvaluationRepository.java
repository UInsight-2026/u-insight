package gt.edu.uinsight.analytics.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.trend.entity.Evaluation;

@Repository("trendEvaluationRepository")
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
