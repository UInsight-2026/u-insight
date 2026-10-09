package gt.edu.uinsight.teacher.repository;

import gt.edu.uinsight.analytics.trend.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acceso de solo consulta y asignacion sobre la tabla de secciones, que es
 * propiedad de otro modulo. El modulo de docentes la usa para conocer su carga
 * academica y para validar las reglas de asignacion y de baja.
 *
 * La entidad Section es una vista de solo lectura de la celula B4 y no expone
 * setters, por lo que la asignacion del docente se hace con una sentencia de
 * actualizacion acotada a la columna teacher_id, sin mutar la entidad ajena.
 */
@Repository
public interface TeacherSectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByTeacherIdOrderBySectionCodeAsc(Long teacherId);

    boolean existsByTeacherId(Long teacherId);

    /** Reasigna la seccion al docente indicado. Devuelve las filas afectadas. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update TrendSection s set s.teacherId = :teacherId where s.id = :sectionId")
    int assignTeacher(@Param("sectionId") Long sectionId, @Param("teacherId") Long teacherId);
}
