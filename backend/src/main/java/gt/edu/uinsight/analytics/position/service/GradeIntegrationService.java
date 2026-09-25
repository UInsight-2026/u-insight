package gt.edu.uinsight.analytics.position.service;

import java.util.List;

public interface GradeIntegrationService {
    List<Double> getGradesBySection(Long sectionId);
    List<Double> getGradesByStudent(Long studentId);

    /**
     * Resuelve la seccion a la que pertenece un estudiante, necesaria para
     * comparar su promedio contra las notas de toda la seccion.
     */
    Long getSectionIdByStudent(Long studentId);
}