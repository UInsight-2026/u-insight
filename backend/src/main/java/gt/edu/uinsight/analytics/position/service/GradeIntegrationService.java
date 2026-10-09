package gt.edu.uinsight.analytics.position.service;

import java.util.List;

public interface GradeIntegrationService {
    List<Double> getGradesBySection(Long sectionId);
    List<Double> getGradesByStudent(Long studentId);

    /**
     * Secciones en las que esta matriculado el estudiante. Un estudiante
     * puede estar matriculado en mas de una (una por curso), por lo que
     * no se puede asumir una unica seccion "del" estudiante.
     */
    List<Long> getSectionIdsByStudent(Long studentId);

    /**
     * Verifica que el estudiante este matriculado en la seccion indicada.
     */
    boolean isStudentEnrolledInSection(Long studentId, Long sectionId);
}