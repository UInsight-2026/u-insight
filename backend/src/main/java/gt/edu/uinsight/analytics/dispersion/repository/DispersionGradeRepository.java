
package gt.edu.uinsight.analytics.dispersion.repository;

import gt.edu.uinsight.analytics.dispersion.entity.DispersionGrade;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import gt.edu.uinsight.analytics.dispersion.entity.DispersionGrade;

=======
>>>>>>> develop
@Repository("dispersionGradeRepository")
public interface DispersionGradeRepository
        extends JpaRepository<DispersionGrade, Long> {

    @Query(value = """
<<<<<<< HEAD
        SELECT g.*
        FROM grade g
        INNER JOIN evaluation e
            ON g.evaluation_id = e.id
        WHERE e.section_id = :sectionId
        """, nativeQuery = true)
    List<DispersionGrade> findGradesBySectionId(
            @Param("sectionId") Long sectionId
    );

    @Query(value = """
        SELECT g.*
        FROM grade g
        INNER JOIN evaluation e
            ON g.evaluation_id = e.id
        INNER JOIN section s
            ON e.section_id = s.id
        WHERE s.course_id = :courseId
        """, nativeQuery = true)
    List<DispersionGrade> findGradesByCourseId(
            @Param("courseId") Long courseId
    );
=======
            SELECT g.score
            FROM grade g
            INNER JOIN evaluation e ON g.evaluation_id = e.id
            WHERE e.section_id = :sectionId
            """, nativeQuery = true)
    List<BigDecimal> findScoresBySectionId(
            @Param("sectionId") Long sectionId);

    @Query(value = """
            SELECT g.score
            FROM grade g
            INNER JOIN evaluation e ON g.evaluation_id = e.id
            INNER JOIN section s ON e.section_id = s.id
            WHERE s.course_id = :courseId
            """, nativeQuery = true)
    List<BigDecimal> findScoresByCourseId(
            @Param("courseId") Long courseId);
>>>>>>> develop
}
