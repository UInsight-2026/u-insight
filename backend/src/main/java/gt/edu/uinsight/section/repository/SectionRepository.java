package gt.edu.uinsight.section.repository;

import gt.edu.uinsight.section.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByTeacherIdOrderByAcademicTermDescSectionCodeAsc(Long teacherId);

    boolean existsByTeacherId(Long teacherId);

    boolean existsBySectionCodeIgnoreCase(String sectionCode);
}
