package gt.edu.uinsight.teacher.repository;

import gt.edu.uinsight.analytics.trend.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso de solo consulta y asignacion sobre la tabla de secciones, que es
 * propiedad de otro modulo. El modulo de docentes la usa para conocer su carga
 * academica y para validar las reglas de asignacion y de baja.
 */
@Repository
public interface TeacherSectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByTeacherIdOrderBySectionCodeAsc(Long teacherId);

    boolean existsByTeacherId(Long teacherId);
}
