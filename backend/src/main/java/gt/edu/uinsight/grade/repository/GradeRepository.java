package gt.edu.uinsight.grade.repository;

import gt.edu.uinsight.grade.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    boolean existsByEvaluationIdAndStudentId(Long evaluationId, Long studentId);

    Optional<Grade> findByEvaluationIdAndStudentId(Long evaluationId, Long studentId);

    List<Grade> findByEvaluationId(Long evaluationId);

    List<Grade> findByStudentId(Long studentId);

    @Query("""
        select g from Grade g
        where g.evaluationId in (
            select e.id from Evaluation e where e.sectionId = :sectionId
        )
        """)
    List<Grade> findBySectionId(@Param("sectionId") Long sectionId);
}
