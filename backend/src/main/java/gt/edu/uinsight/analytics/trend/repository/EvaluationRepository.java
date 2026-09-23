package gt.edu.uinsight.analytics.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gt.edu.uinsight.analytics.trend.entity.Evaluation;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
