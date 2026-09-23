package gt.edu.uinsight.evaluation.repository;

import gt.edu.uinsight.evaluation.entity.Evaluation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    // Usado por GET /api/v1/sections/{id}/evaluations (HU2)
    List<Evaluation> findBySectionId(Long sectionId);
}
