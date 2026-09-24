package gt.edu.uinsight.grade.repository;

import gt.edu.uinsight.grade.model.EvaluationRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRefRepository extends JpaRepository<EvaluationRef, Long> {

    List<EvaluationRef> findBySectionId(Long sectionId);
}
