package gt.edu.uinsight.analytics.dispersion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.dispersion.entity.Grade;

// La célula A6 (grade) y la célula B4 (trend) también declaran una interfaz GradeRepository
// sobre la misma tabla; Spring deriva el nombre del bean del nombre simple de la interfaz,
// así que colisionaban por 'gradeRepository' y el contexto no arrancaba
// (BeanDefinitionOverrideException). Se nombra explícitamente este bean, igual que ya se
// hizo para el de la célula B4 (ver trend/repository/GradeRepository.java); la inyección
// por tipo dentro de este módulo no cambia.
@Repository("dispersionGradeRepository")
public interface GradeRepository extends JpaRepository<Grade, Long> {

    @Query(value = "SELECT g.* FROM grade g INNER JOIN evaluation e ON g.evaluation_id = e.id WHERE e.section_id = :sectionId", nativeQuery = true)
    List<Grade> findGradesBySectionId(@Param("sectionId") Long sectionId);

    @Query(value = "SELECT g.* FROM grade g INNER JOIN evaluation e ON g.evaluation_id = e.id INNER JOIN section s ON e.section_id = s.id WHERE s.course_id = :courseId", nativeQuery = true)
    List<Grade> findGradesByCourseId(@Param("courseId") Long courseId);
}