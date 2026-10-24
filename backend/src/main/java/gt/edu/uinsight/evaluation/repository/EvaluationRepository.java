package gt.edu.uinsight.evaluation.repository;

import gt.edu.uinsight.evaluation.entity.Evaluation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    // HU2: GET /api/v1/sections/{id}/evaluations
    List<Evaluation> findBySectionId(Long sectionId);
}