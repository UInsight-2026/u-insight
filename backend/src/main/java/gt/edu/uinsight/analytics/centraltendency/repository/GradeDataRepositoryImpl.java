package gt.edu.uinsight.analytics.centraltendency.repository;

import gt.edu.uinsight.analytics.centraltendency.exception.AnalyticsResourceNotFoundException;
import gt.edu.uinsight.analytics.centraltendency.exception.InvalidAnalyticsRequestException;
import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GradeDataRepositoryImpl implements GradeDataRepository {

    private final AnalyticsDatabaseRepository databaseRepository;

    public GradeDataRepositoryImpl(AnalyticsDatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    @Override
    public List<gt.edu.uinsight.analytics.centraltendency.model.GradeData> findBySectionId(
            Long sectionId, Long evaluationId) {
        if (!databaseRepository.sectionExists(sectionId)) {
            throw new AnalyticsResourceNotFoundException("La seccion " + sectionId + " no existe");
        }
        if (evaluationId != null
                && !databaseRepository.evaluationBelongsToSection(evaluationId, sectionId)) {
            throw new InvalidAnalyticsRequestException(
                    "La evaluacion " + evaluationId + " no pertenece a la seccion " + sectionId);
        }

        List<AnalyticsDatabaseRepository.AnalyticsGrade> grades = evaluationId == null
                ? databaseRepository.findGradesBySection(sectionId)
                : databaseRepository.findGradesBySectionAndEvaluation(sectionId, evaluationId);
        return grades.stream()
                .map(AnalyticsDatabaseRepository.AnalyticsGrade::toGradeData)
                .toList();
    }

    @Override
    public List<gt.edu.uinsight.analytics.centraltendency.model.GradeData> findByCourseId(
            Long courseId, Long periodId) {
        if (!databaseRepository.courseExists(courseId)) {
            throw new AnalyticsResourceNotFoundException("El curso " + courseId + " no existe");
        }
        if (periodId != null && !databaseRepository.periodExists(periodId)) {
            throw new InvalidAnalyticsRequestException(
                    "El periodo academico " + periodId + " no es valido");
        }

        return databaseRepository.findGradesByCourse(courseId, periodId).stream()
                .map(AnalyticsDatabaseRepository.AnalyticsGrade::toGradeData)
                .toList();
    }
}
