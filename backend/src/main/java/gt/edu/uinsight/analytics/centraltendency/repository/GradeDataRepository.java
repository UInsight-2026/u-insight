package gt.edu.uinsight.analytics.centraltendency.repository;

import java.util.List;

import gt.edu.uinsight.analytics.centraltendency.model.GradeData;

public interface GradeDataRepository {
    List<GradeData> findBySectionId(Long sectionId);
    List<GradeData> findByCourseId(Long courseId);
}
