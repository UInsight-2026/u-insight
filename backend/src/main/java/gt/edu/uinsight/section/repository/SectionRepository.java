package gt.edu.uinsight.section.repository;

import gt.edu.uinsight.section.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByAcademicPeriodId(Long academicPeriodId);
    List<Section> findByCourseId(Long courseId);
    List<Section> findByTeacherId(Long teacherId);
    boolean existsByAcademicPeriodIdAndCourseIdAndSectionCode(Long academicPeriodId, Long courseId, String sectionCode);
}
