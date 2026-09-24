package gt.edu.uinsight.grade.repository;

import gt.edu.uinsight.grade.model.EnrollmentRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRefRepository extends JpaRepository<EnrollmentRef, Long> {

    boolean existsByStudentIdAndSectionId(Long studentId, Long sectionId);
}
