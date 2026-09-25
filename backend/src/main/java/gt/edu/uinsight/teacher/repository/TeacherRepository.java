package gt.edu.uinsight.teacher.repository;

import gt.edu.uinsight.teacher.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByTeacherCodeIgnoreCase(String teacherCode);

    boolean existsByTeacherCodeIgnoreCase(String teacherCode);

    boolean existsByTeacherCodeIgnoreCaseAndIdNot(String teacherCode, Long id);
}
