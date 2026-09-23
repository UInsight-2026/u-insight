package gt.edu.uinsight.report.mock;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockCourse;
import gt.edu.uinsight.report.mock.model.MockSection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Capa comun de datos de la Celula C5.
 *
 * En el sistema real estos metodos se convierten en llamadas a los
 * servicios de las Celulas A, B y C. Para la Semana 2 se simulan con
 * listas en memoria.
 *
 * IMPORTANTE: al reemplazar esto por llamadas reales, cada llamada debe
 * ir en su propio try/catch (Escenario 4.2 del diseno de C5): si una
 * celula no responde, el reporte devuelve lo que si tiene.
 */
@Component
public class MockDataGateway {

    private final List<MockCourse> courses = new ArrayList<>();
    private final List<MockSection> sections = new ArrayList<>();
    private final List<MockAlert> alerts = new ArrayList<>();

    public MockDataGateway() {
        seedCourses();
        seedSections();
        seedAlerts();
    }

    // Cursos (simulan a la Celula A1)

    public List<MockCourse> findCourses(ReportFilter filter) {
        return courses.stream()
                .filter(c -> matches(filter.getPeriod(), c.getPeriod()))
                .filter(c -> matches(filter.getCourse(), c.getCode()))
                .filter(c -> matches(filter.getTeacher(), c.getTeacherCode()))
                .collect(Collectors.toList());
    }

    public Optional<MockCourse> findCourseById(Long id) {
        return courses.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    // Secciones (simulan a A4 + el riesgo calculado por B7)

    public List<MockSection> findSections(ReportFilter filter) {
        return sections.stream()
                .filter(s -> matches(filter.getPeriod(), s.getPeriod()))
                .filter(s -> matches(filter.getCourse(), s.getCourseCode()))
                .filter(s -> matches(filter.getTeacher(), s.getTeacherCode()))
                .filter(s -> matches(filter.getSection(), s.getName()))
                .filter(s -> matches(filter.getRiskLevel(), s.getRiskLevel()))
                .collect(Collectors.toList());
    }

    public Optional<MockSection> findSectionById(Long id) {
        return sections.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    public List<MockSection> findSectionsByCourseId(Long courseId) {
        return sections.stream().filter(s -> s.getCourseId().equals(courseId)).collect(Collectors.toList());
    }

    // Alertas (simulan a C2 + C3)

    public List<MockAlert> findAlerts(ReportFilter filter) {
        return alerts.stream()
                .filter(a -> matches(filter.getPeriod(), a.getPeriod()))
                .filter(a -> matches(filter.getCourse(), a.getCourseCode()))
                .filter(a -> matches(filter.getTeacher(), a.getTeacherCode()))
                .filter(a -> matches(filter.getSection(), a.getSectionName()))
                .filter(a -> matches(filter.getRiskLevel(), a.getRiskLevel()))
                .filter(a -> matches(filter.getAlertStatus(), a.getStatus()))
                .collect(Collectors.toList());
    }

    public List<MockAlert> findAlertsBySectionId(Long sectionId) {
        return alerts.stream().filter(a -> a.getSectionId().equals(sectionId)).collect(Collectors.toList());
    }

    public List<MockAlert> findAlertsByCourseId(Long courseId) {
        return alerts.stream().filter(a -> a.getCourseId().equals(courseId)).collect(Collectors.toList());
    }

    // Un filtro nulo o vacio significa "no filtrar"

    private boolean matches(String filterValue, String actualValue) {
        return filterValue == null || filterValue.isBlank() || actualValue.equalsIgnoreCase(filterValue);
    }

    // Datos simulados: los numeros de /overview sin filtros (12 alertas
    // activas, 4 secciones en alto riesgo, 36 estudiantes en riesgo,
    // tendencia NEGATIVE) coinciden a proposito con el ejemplo del
    // documento de diseno de la Semana 1.

    private void seedCourses() {
        courses.add(new MockCourse(1L, "PROG2", "Programacion II", "DOC-101", "Cristopher Munoz", "2026-2", 120));
        courses.add(new MockCourse(2L, "BD1", "Bases de Datos I", "DOC-102", "Ana Paredes", "2026-2", 95));
        courses.add(new MockCourse(3L, "EST1", "Estadistica I", "DOC-103", "Marco Ramirez", "2026-2", 110));
    }

    private void seedSections() {
        // Programacion II
        sections.add(new MockSection(10L, "A", 1L, "PROG2", "DOC-101", "2026-2", "HIGH", 8));
        sections.add(new MockSection(11L, "B", 1L, "PROG2", "DOC-101", "2026-2", "MEDIUM", 4));
        sections.add(new MockSection(12L, "C", 1L, "PROG2", "DOC-101", "2026-2", "LOW", 1));
        // Bases de Datos I
        sections.add(new MockSection(20L, "A", 2L, "BD1", "DOC-102", "2026-2", "HIGH", 7));
        sections.add(new MockSection(21L, "B", 2L, "BD1", "DOC-102", "2026-2", "HIGH", 6));
        sections.add(new MockSection(22L, "C", 2L, "BD1", "DOC-102", "2026-2", "MEDIUM", 3));
        // Estadistica I
        sections.add(new MockSection(30L, "A", 3L, "EST1", "DOC-103", "2026-2", "HIGH", 5));
        sections.add(new MockSection(31L, "B", 3L, "EST1", "DOC-103", "2026-2", "MEDIUM", 2));
        sections.add(new MockSection(32L, "C", 3L, "EST1", "DOC-103", "2026-2", "LOW", 0));
    }

    private void seedAlerts() {
        // Seccion 10 (PROG2-A, HIGH) -> 5 alertas activas
        alerts.add(new MockAlert(1001L, 10L, "A", 1L, "PROG2", "DOC-101", "2026-2",
                "PERFORMANCE", "HIGH", "NEW",
                "Alto porcentaje de estudiantes con rendimiento bajo", "2026-09-08T09:15:00"));
        alerts.add(new MockAlert(1002L, 10L, "A", 1L, "PROG2", "DOC-101", "2026-2",
                "TREND", "HIGH", "UNDER_REVIEW",
                "Tendencia negativa en las ultimas evaluaciones", "2026-09-09T11:00:00"));
        alerts.add(new MockAlert(1003L, 10L, "A", 1L, "PROG2", "DOC-101", "2026-2",
                "DISPERSION", "MEDIUM", "NEW",
                "Alta dispersion en los resultados", "2026-09-09T15:40:00"));
        alerts.add(new MockAlert(1004L, 10L, "A", 1L, "PROG2", "DOC-101", "2026-2",
                "PERFORMANCE", "HIGH", "IN_PROGRESS",
                "Estudiantes en riesgo critico sin intervencion registrada", "2026-09-10T08:20:00"));
        alerts.add(new MockAlert(1005L, 10L, "A", 1L, "PROG2", "DOC-101", "2026-2",
                "TREND", "HIGH", "NEW",
                "Caida sostenida en el promedio de la seccion", "2026-09-11T10:05:00"));

        // Seccion 11 (PROG2-B, MEDIUM)
        alerts.add(new MockAlert(1006L, 11L, "B", 1L, "PROG2", "DOC-101", "2026-2",
                "DISPERSION", "MEDIUM", "NEW",
                "Variabilidad significativa entre estudiantes", "2026-09-07T13:10:00"));

        // Seccion 12 (PROG2-C, LOW) -> alerta resuelta, no cuenta como activa
        alerts.add(new MockAlert(1007L, 12L, "C", 1L, "PROG2", "DOC-101", "2026-2",
                "PERFORMANCE", "LOW", "RESOLVED",
                "Riesgo de rendimiento resuelto tras tutorias", "2026-08-30T09:00:00"));

        // Seccion 20 (BD1-A, HIGH)
        alerts.add(new MockAlert(1008L, 20L, "A", 2L, "BD1", "DOC-102", "2026-2",
                "PERFORMANCE", "HIGH", "NEW",
                "Bajo rendimiento sostenido en la seccion", "2026-09-06T10:00:00"));
        alerts.add(new MockAlert(1009L, 20L, "A", 2L, "BD1", "DOC-102", "2026-2",
                "TREND", "HIGH", "IN_PROGRESS",
                "Tendencia negativa detectada entre evaluaciones", "2026-09-08T16:30:00"));

        // Seccion 21 (BD1-B, HIGH)
        alerts.add(new MockAlert(1010L, 21L, "B", 2L, "BD1", "DOC-102", "2026-2",
                "PERFORMANCE", "HIGH", "UNDER_REVIEW",
                "Alto porcentaje de estudiantes en riesgo", "2026-09-05T09:45:00"));
        alerts.add(new MockAlert(1011L, 21L, "B", 2L, "BD1", "DOC-102", "2026-2",
                "DISPERSION", "MEDIUM", "NEW",
                "Dispersion alta entre los resultados de la seccion", "2026-09-10T14:00:00"));

        // Seccion 22 (BD1-C, MEDIUM)
        alerts.add(new MockAlert(1012L, 22L, "C", 2L, "BD1", "DOC-102", "2026-2",
                "TREND", "MEDIUM", "NEW",
                "Tendencia negativa moderada", "2026-09-11T12:15:00"));

        // Seccion 30 (EST1-A, HIGH)
        alerts.add(new MockAlert(1013L, 30L, "A", 3L, "EST1", "DOC-103", "2026-2",
                "PERFORMANCE", "HIGH", "NEW",
                "Rendimiento bajo en mas de la mitad de estudiantes", "2026-09-09T08:50:00"));

        // Seccion 31 (EST1-B, MEDIUM) -> alerta descartada, no cuenta como activa
        alerts.add(new MockAlert(1014L, 31L, "B", 3L, "EST1", "DOC-103", "2026-2",
                "DISPERSION", "MEDIUM", "DISMISSED",
                "Alerta descartada tras revision del docente", "2026-09-04T11:20:00"));
    }
}
