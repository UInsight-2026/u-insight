package gt.edu.uinsight.student.repository;

import gt.edu.uinsight.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByOrderByIdAsc();

    Optional<Student> findByStudentCodeIgnoreCase(String studentCode);

    boolean existsByStudentCodeIgnoreCase(String studentCode);
}