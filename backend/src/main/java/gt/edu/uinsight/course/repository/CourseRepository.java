package gt.edu.uinsight.course.repository;

import gt.edu.uinsight.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCodeIgnoreCase(String code);
}
