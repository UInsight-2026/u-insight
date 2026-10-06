package gt.edu.uinsight.analytics.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gt.edu.uinsight.evaluation.entity.Evaluation;

public interface TrendEvaluationRepository extends JpaRepository<Evaluation, Long> {
}