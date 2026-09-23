package gt.edu.uinsight.evaluation.repository;

import gt.edu.uinsight.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    // HU2: GET /api/v1/sections/{id}/evaluations
    List<Evaluation> findBySectionId(Long sectionId);
}
