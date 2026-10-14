package gt.edu.uinsight.analytics.dispersion.repository;

import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository("dispersionGradeRepository")
public class DispersionGradeRepository {

    private final AnalyticsDatabaseRepository databaseRepository;

    public DispersionGradeRepository(AnalyticsDatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public List<BigDecimal> findScoresBySectionId(Long sectionId) {
        return databaseRepository.findGradesBySection(sectionId).stream()
                .map(AnalyticsDatabaseRepository.AnalyticsGrade::score)
                .toList();
    }

    public List<BigDecimal> findScoresByCourseId(Long courseId) {
        return databaseRepository.findGradesByCourse(courseId, null).stream()
                .map(AnalyticsDatabaseRepository.AnalyticsGrade::score)
                .toList();
    }

    public boolean courseExists(Long courseId) {
        return databaseRepository.courseExists(courseId);
    }
}
