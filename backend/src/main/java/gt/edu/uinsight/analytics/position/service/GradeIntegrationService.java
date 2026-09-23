package gt.edu.uinsight.analytics.position.service;

import java.util.List;

public interface GradeIntegrationService {
    List<Double> getGradesBySection(Long sectionId);
    List<Double> getGradesByStudent(Long studentId);
}