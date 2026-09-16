package com.uinsight.academic.enrollment.repository;

import com.uinsight.academic.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findBySectionId(Long sectionId);
    boolean existsByStudentIdAndSectionIdAndStatus(Long studentId, Long sectionId, String status);
}