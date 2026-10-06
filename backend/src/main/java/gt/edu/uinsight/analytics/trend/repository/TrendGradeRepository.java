package gt.edu.uinsight.analytics.trend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gt.edu.uinsight.grade.model.Grade;

public interface TrendGradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentId(Long studentId);
}