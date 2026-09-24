package gt.edu.uinsight.analytics.dispersion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.dispersion.entity.Grade;

@Repository("dispersionGradeRepository")
public interface GradeRepository extends JpaRepository<Grade, Long> {

    @Query(value = """
        SELECT g.*
        FROM grade g
        INNER JOIN evaluation e
            ON g.evaluation_id = e.id
        WHERE e.section_id = :sectionId
        """, nativeQuery = true)
    List<Grade> findGradesBySectionId(
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
    List<Grade> findGradesByCourseId(
            @Param("courseId") Long courseId
    );
}