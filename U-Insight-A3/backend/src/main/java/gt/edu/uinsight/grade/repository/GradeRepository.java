package gt.edu.uinsight.grade.repository;

import gt.edu.uinsight.grade.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudent_IdOrderByRegisteredAtDescIdDesc(Long studentId);
}