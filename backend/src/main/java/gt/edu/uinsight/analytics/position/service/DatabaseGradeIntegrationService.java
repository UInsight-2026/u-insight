package gt.edu.uinsight.analytics.position.service;

import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;

@Service
public class DatabaseGradeIntegrationService implements GradeIntegrationService {

    private final AnalyticsDatabaseRepository databaseRepository;

    public DatabaseGradeIntegrationService(AnalyticsDatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    @Override
    public List<Double> getGradesBySection(Long sectionId) {
        return databaseRepository.findGradesBySection(sectionId).stream()
                .map(grade -> normalize(grade.score(), grade.maximumScore()))
                .toList();
    }

    @Override
    public List<Double> getGradesByStudent(Long studentId) {
        return databaseRepository.findGradesByStudent(studentId).stream()
                .map(grade -> normalize(grade.score(), grade.maximumScore()))
                .toList();
    }

    @Override
    public List<Double> getGradesByStudent(Long studentId, Long sectionId) {
        return databaseRepository.findGradesByStudentAndSection(studentId, sectionId).stream()
                .map(grade -> normalize(grade.score(), grade.maximumScore()))
                .toList();
    }

    @Override
    public String getStudentCode(Long studentId) {
        return databaseRepository.findStudentCode(studentId);
    }

    @Override
    public List<Long> getSectionIdsByStudent(Long studentId) {
        return databaseRepository.findActiveSectionIdsByStudent(studentId);
    }

    @Override
    public boolean isStudentEnrolledInSection(Long studentId, Long sectionId) {
        return databaseRepository.findActiveSectionIdsByStudent(studentId).contains(sectionId);
    }

    private Double normalize(java.math.BigDecimal score, java.math.BigDecimal maximumScore) {
        if (score == null || maximumScore == null || maximumScore.signum() <= 0) {
            throw new IllegalStateException("La nota y la nota máxima deben ser válidas para calcular posiciones.");
        }
        return score.divide(maximumScore, 6, RoundingMode.HALF_UP)
                .multiply(java.math.BigDecimal.valueOf(100))
                .doubleValue();
    }
}
