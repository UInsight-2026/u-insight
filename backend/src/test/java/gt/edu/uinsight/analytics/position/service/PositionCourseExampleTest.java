package gt.edu.uinsight.analytics.position.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Ejemplo resuelto en clase (Estadistica I): D4 con n = 10 datos.
 * Posicion = (4 * 10) / 10 + 1/2 = 4.5; interpolando entre P4 = 46.3 y P5 = 46.5
 * con Fp = 0.5 se obtiene 46.4.
 */
class PositionCourseExampleTest {

    private static final List<Double> NOTAS = List.of(
            46.1, 46.2, 46.3, 46.3, 46.5, 46.7, 46.9, 46.9, 47.2, 48.3);

    @Test
    void percentile40EqualsDecile4FromCourseExample() {
        GradeIntegrationService fuente = new GradeIntegrationService() {
            @Override public List<Double> getGradesBySection(Long sectionId) { return NOTAS; }
            @Override public List<Double> getGradesByStudent(Long studentId) { return NOTAS; }
            @Override public List<Long> getSectionIdsByStudent(Long studentId) { return List.of(); }
            @Override public boolean isStudentEnrolledInSection(Long studentId, Long sectionId) { return false; }
        };

        PositionService service = new PositionService(fuente);

        var response = service.getSectionPosition(1L, List.of(40));

        assertThat(response.getPercentiles().get("P40")).isEqualTo(46.4, org.assertj.core.data.Offset.offset(0.0001));
    }
}
