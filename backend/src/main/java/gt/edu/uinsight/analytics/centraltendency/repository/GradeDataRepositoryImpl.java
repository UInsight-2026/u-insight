package gt.edu.uinsight.analytics.centraltendency.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.centraltendency.model.GradeData;

/**
 * Implementacion TEMPORAL con datos simulados, siguiendo el mismo patron
 * que AnalyticsClientRepositoryImpl (B6), mientras el modulo A6
 * (importacion/consulta de calificaciones) entrega la integracion real.
 */
@Repository
public class GradeDataRepositoryImpl implements GradeDataRepository {

    @Override
    public List<GradeData> findBySectionId(Long sectionId) {
        return List.of(
                new GradeData(1L, sectionId, 100L, BigDecimal.valueOf(70.0)),
                new GradeData(2L, sectionId, 100L, BigDecimal.valueOf(75.0)),
                new GradeData(3L, sectionId, 100L, BigDecimal.valueOf(80.0)),
                new GradeData(4L, sectionId, 100L, BigDecimal.valueOf(70.0)),
                new GradeData(5L, sectionId, 100L, BigDecimal.valueOf(90.0))
        );
    }

    @Override
    public List<GradeData> findByCourseId(Long courseId) {
        return List.of(
                new GradeData(1L, 200L, courseId, BigDecimal.valueOf(65.0)),
                new GradeData(2L, 201L, courseId, BigDecimal.valueOf(72.0)),
                new GradeData(3L, 202L, courseId, BigDecimal.valueOf(78.0)),
                new GradeData(4L, 203L, courseId, BigDecimal.valueOf(78.0)),
                new GradeData(5L, 204L, courseId, BigDecimal.valueOf(85.0))
        );
    }
}
