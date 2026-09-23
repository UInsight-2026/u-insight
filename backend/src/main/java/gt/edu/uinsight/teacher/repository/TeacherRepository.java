package gt.edu.uinsight.teacher.repository;

import gt.edu.uinsight.teacher.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}