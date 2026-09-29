package gt.edu.uinsight.analytics.trend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import gt.edu.uinsight.analytics.trend.entity.Grade;

/** Consultas de lectura que alimentan el cálculo de tendencias. */
public interface TrendRepository extends Repository<Grade, Long> {

    @Query(value = """
            SELECT g.*
              FROM grade g
              JOIN evaluation e ON e.id = g.evaluation_id
             WHERE e.section_id = :sectionId
               AND e.status = 'CLOSED'
             ORDER BY e.evaluation_date ASC
            """, nativeQuery = true)
    List<Grade> findSectionGradesOrdered(@Param("sectionId") Long sectionId);

    @Query(value = """
            SELECT g.*
              FROM grade g
              JOIN evaluation e ON e.id = g.evaluation_id
             WHERE g.student_id = :studentId
               AND e.status = 'CLOSED'
             ORDER BY e.evaluation_date ASC
            """, nativeQuery = true)
    List<Grade> findStudentGradesOrdered(@Param("studentId") Long studentId);
}
