package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import gt.edu.uinsight.academicperiod.entity.PeriodStatus;
import gt.edu.uinsight.academicperiod.repository.AcademicPeriodRepository;
import gt.edu.uinsight.enrollment.entity.Enrollment;
import gt.edu.uinsight.enrollment.repository.EnrollmentRepository;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.RiskSnapshot;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockSection;
import gt.edu.uinsight.section.entity.Section;
import gt.edu.uinsight.section.repository.SectionRepository;
import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaReportDataGatewayTest {
    private final SectionRepository sections = mock(SectionRepository.class);
    private final AcademicPeriodRepository periods = mock(AcademicPeriodRepository.class);
    private final TeacherRepository teachers = mock(TeacherRepository.class);
    private final EnrollmentRepository enrollments = mock(EnrollmentRepository.class);
    private final RiskGateway risk = mock(RiskGateway.class);
    private final AlertQueryPort alerts = mock(AlertQueryPort.class);
    private final JpaReportDataGateway gateway = new JpaReportDataGateway(
            sections, periods, teachers, enrollments, risk, alerts);

    @BeforeEach
    void catalogos() {
        when(sections.findById(10L)).thenReturn(Optional.of(section(10L, 7L)));
        when(teachers.findById(7L)).thenReturn(Optional.of(teacher(7L, "DOC-01")));
        when(periods.findById(3L)).thenReturn(Optional.of(period("2026-1", 1)));
        when(risk.getSectionRisk(10L)).thenReturn(new RiskSnapshot("HIGH", "B7", true));
    }

    @Test
    void traduceSeccionDeA4SinInventarCursoNiEstudiantesEnRiesgo() {
        MockSection result = gateway.findSectionById(10L).orElseThrow();
        assertEquals(10L, result.getId());
        assertEquals("A", result.getName());
        assertEquals(5L, result.getCourseId());
        assertEquals("DOC-01", result.getTeacherCode());
        assertEquals("2026-1", result.getPeriod());
        assertEquals("HIGH", result.getRiskLevel());
        assertNull(result.getCourseCode());
        assertEquals(0, result.getStudentsAtRisk());
    }

    @Test
    void periodoInexistenteDevuelveVacioSinConsultarA4() {
        when(periods.findAll()).thenReturn(List.of(period("2026-1", 1)));
        assertTrue(gateway.findSections(filter("2025-1", null, null, null, null)).isEmpty());
        verify(sections, never()).findAll();
        verify(sections, never()).findByAcademicPeriodId(anyLong());
    }

    @Test
    void falloDeA4NoSePropagaEnNingunaConsultaDeSecciones() {
        when(sections.findAll()).thenThrow(new IllegalStateException("A4 caida"));
        when(sections.findById(10L)).thenThrow(new IllegalStateException("A4 caida"));
        when(sections.findByCourseId(5L)).thenThrow(new IllegalStateException("A4 caida"));
        assertTrue(gateway.findSections(null).isEmpty());
        assertTrue(gateway.findSectionById(10L).isEmpty());
        assertTrue(gateway.findSectionsByCourseId(5L).isEmpty());
    }

    @Test
    void riesgoDesconocidoNuncaSeConvierteEnBajoNiEliminaLaSeccion() {
        when(risk.getSectionRisk(10L)).thenReturn(RiskSnapshot.unavailable());
        assertNull(gateway.findSectionById(10L).orElseThrow().getRiskLevel());
        when(risk.getSectionRisk(10L)).thenReturn(null);
        assertNull(gateway.findSectionById(10L).orElseThrow().getRiskLevel());
        when(risk.getSectionRisk(10L)).thenThrow(new IllegalStateException("B7 caida"));
        assertNull(gateway.findSectionById(10L).orElseThrow().getRiskLevel());
    }

    @Test
    void falloDeA2ConservaLaSeccionYLosOtrosDatos() {
        when(teachers.findById(7L)).thenThrow(new IllegalStateException("A2 caida"));
        MockSection result = gateway.findSectionById(10L).orElseThrow();
        assertNull(result.getTeacherCode());
        assertEquals("2026-1", result.getPeriod());
        assertEquals("HIGH", result.getRiskLevel());
    }

    @Test
    void cuentaInscripcionesYDegradaSiFallaA4() {
        when(enrollments.findBySectionId(10L)).thenReturn(List.of(new Enrollment(), new Enrollment()));
        assertEquals(2, gateway.countEnrolledStudents(10L));
        assertEquals(0, gateway.countEnrolledStudents(999L));
        when(enrollments.findBySectionId(10L)).thenThrow(new IllegalStateException("A4 caida"));
        assertEquals(0, gateway.countEnrolledStudents(10L));
    }

    @Test
    void combinaPeriodoDocenteSeccionYRiesgoSinIgnorarDocente() {
        AcademicPeriod period = mock(AcademicPeriod.class);
        when(period.getId()).thenReturn(3L);
        when(period.getName()).thenReturn("2026-1");
        when(periods.findAll()).thenReturn(List.of(period));
        when(sections.findByAcademicPeriodId(3L)).thenReturn(List.of(section(10L, 7L), section(11L, 8L)));
        when(teachers.findById(8L)).thenReturn(Optional.of(teacher(8L, "DOC-02")));
        when(risk.getSectionRisk(11L)).thenReturn(new RiskSnapshot("HIGH", "B7", true));
        assertEquals(List.of(10L), gateway.findSections(filter("2026-1", "doc-01", "a", "high", null))
                .stream().map(MockSection::getId).toList());
    }

    @Test
    void consultaPorDocenteYSemestreDeducidoDeFecha() {
        when(teachers.findByTeacherCodeIgnoreCase("doc-01")).thenReturn(Optional.of(teacher(7L, "DOC-01")));
        when(sections.findByTeacherId(7L)).thenReturn(List.of(section(10L, 7L)));
        when(periods.findById(3L)).thenReturn(Optional.of(period("Segundo semestre", 7)));
        assertEquals("2026-2", gateway.findSections(filter(null, "doc-01", null, null, null)).getFirst().getPeriod());
        verify(sections).findByTeacherId(7L);
        assertTrue(gateway.findSections(filter(null, "DOC-99", null, null, null)).isEmpty());
    }

    @Test
    void falloDeA1NoOcultaSeccionesSinFiltroDePeriodo() {
        when(periods.findById(3L)).thenThrow(new IllegalStateException("A1 caida"));
        assertNull(gateway.findSectionById(10L).orElseThrow().getPeriod());
        when(periods.findAll()).thenThrow(new IllegalStateException("A1 caida"));
        assertTrue(gateway.findSections(filter("2026-1", null, null, null, null)).isEmpty());
    }

    @Test
    void cursosNoDisponiblesNoProducenDatosSimuladosNiIgnoranElFiltro() {
        assertTrue(gateway.findCourses(null).isEmpty());
        assertTrue(gateway.findCourseById(5L).isEmpty());
        when(sections.findAll()).thenReturn(List.of(section(10L, 7L)));
        assertTrue(gateway.findSections(new ReportFilter(null, "PROG2", null, null, null, null)).isEmpty());
    }

    @Test
    void alertasSeEnriquecenYFiltranSinRepetirConsultasPorSeccion() {
        when(alerts.findAll()).thenReturn(List.of(alert(1L, 10L, "NEW"), alert(2L, 10L, "RESOLVED")));
        var result = gateway.findAlerts(filter("2026-1", "DOC-01", "A", "HIGH", "NEW"));
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals(5L, result.getFirst().getCourseId());
        assertEquals("DOC-01", result.getFirst().getTeacherCode());
        verify(sections, times(1)).findById(10L);
    }

    @Test
    void alertasPorCursoNoIncluyenOtrosCursos() {
        when(sections.findByCourseId(5L)).thenReturn(List.of(section(10L, 7L)));
        when(alerts.findAll()).thenReturn(List.of(alert(1L, 10L, "NEW"), alert(2L, 12L, "NEW")));
        assertEquals(List.of(1L), gateway.findAlertsByCourseId(5L).stream().map(MockAlert::getId).toList());
    }

    @Test
    void alertasPorSeccionUsanElPuertoYConservanSuEstado() {
        when(alerts.findBySectionId(10L)).thenReturn(List.of(alert(1L, 10L, "NEW")));
        var result = gateway.findAlertsBySectionId(10L);
        assertEquals(1, result.size());
        assertEquals("HIGH", result.getFirst().getRiskLevel());
        assertEquals("NEW", result.getFirst().getStatus());
        verify(alerts, never()).findAll();
    }

    @Test
    void falloDeC3OAusenciaDelPuertoDevuelveAlertasVacias() {
        when(alerts.findAll()).thenThrow(new IllegalStateException("C3 caida"));
        when(alerts.findBySectionId(10L)).thenThrow(new IllegalStateException("C3 caida"));
        assertTrue(gateway.findAlerts(null).isEmpty());
        assertTrue(gateway.findAlertsBySectionId(10L).isEmpty());
        var noPort = new JpaReportDataGateway(sections, periods, teachers, enrollments, risk, null);
        assertTrue(noPort.findAlerts(null).isEmpty());
        assertTrue(noPort.findAlertsBySectionId(10L).isEmpty());
    }

    private Section section(Long id, Long teacherId) {
        return new Section(id, 3L, 5L, teacherId, "A", "ACTIVE");
    }

    private Teacher teacher(Long id, String code) {
        var teacher = new Teacher();
        teacher.setId(id);
        teacher.setTeacherCode(code);
        return teacher;
    }

    private AcademicPeriod period(String name, int month) {
        return new AcademicPeriod(name, 2026, LocalDate.of(2026, month, 1), LocalDate.of(2026, 12, 31), PeriodStatus.ACTIVE);
    }

    private ReportFilter filter(String period, String teacher, String section, String risk, String status) {
        return new ReportFilter(period, null, teacher, section, risk, status);
    }

    private MockAlert alert(Long id, Long sectionId, String status) {
        return new MockAlert(id, sectionId, null, null, null, null, null, null, null, status, null, "2026-09-28T10:00:00");
    }
}
