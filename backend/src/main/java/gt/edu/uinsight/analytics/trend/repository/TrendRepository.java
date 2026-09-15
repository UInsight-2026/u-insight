package gt.edu.uinsight.analytics.trend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.trend.entity.GradeRecord;

@Repository
public interface TrendRepository extends JpaRepository<GradeRecord, Long> {

    List<GradeRecord> findByEvaluation_SectionIdOrderByEvaluation_EvaluationDateAsc(
            Long sectionId);

    List<GradeRecord> findByStudentIdOrderByEvaluation_EvaluationDateAsc(
            Long studentId);
}
