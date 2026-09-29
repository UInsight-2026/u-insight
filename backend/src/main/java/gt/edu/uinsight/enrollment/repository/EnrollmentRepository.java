package gt.edu.uinsight.enrollment.repository;

import gt.edu.uinsight.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findBySectionId(Long sectionId);
    List<Enrollment> findByStudentId(Long studentId);
    boolean existsBySectionIdAndStudentId(Long sectionId, Long studentId);
    Optional<Enrollment> findBySectionIdAndStudentId(Long sectionId, Long studentId);
}