package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import gt.edu.uinsight.academicperiod.entity.PeriodStatus;
import gt.edu.uinsight.academicperiod.repository.AcademicPeriodRepository;
import gt.edu.uinsight.enrollment.entity.Enrollment;
import gt.edu.uinsight.enrollment.repository.EnrollmentRepository;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.RiskSnapshot;
import gt.edu.uinsight.section.entity.Section;
import gt.edu.uinsight.section.repository.SectionRepository;
import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.model.TeacherStatus;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import gt.edu.uinsight.teacher.repository.TeacherSectionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Repositorios reales y esquema H2; solo B7 se sustituye por una fuente no disponible. */
@SpringJUnitConfig(JpaReportDataGatewayIntegrationTest.JpaConfiguration.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:c5-gateway;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;NON_KEYWORDS=YEAR",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class JpaReportDataGatewayIntegrationTest {
    // El contexto se limita a los catalogos consumidos por C5, sin escanear servicios
    // ni entidades duplicadas de otros modulos. No necesita un starter de test nuevo.
    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {AcademicPeriod.class, Teacher.class, Section.class, Enrollment.class})
    @EnableJpaRepositories(basePackageClasses = {AcademicPeriodRepository.class, TeacherRepository.class,
            SectionRepository.class, EnrollmentRepository.class},
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                    classes = TeacherSectionRepository.class))
    static class JpaConfiguration { }

    @Autowired private AcademicPeriodRepository periods;
    @Autowired private TeacherRepository teachers;
    @Autowired private SectionRepository sections;
    @Autowired private EnrollmentRepository enrollments;
    @Autowired private EntityManager entityManager;
    private JpaReportDataGateway gateway;

    @BeforeEach
    void init() {
        gateway = new JpaReportDataGateway(sections, periods, teachers, enrollments,
                id -> RiskSnapshot.unavailable(), null);
    }

    @Test
    @DisplayName("lee la seccion persistida con periodo y docente reales")
    void leeUnaSeccionCompleta() {
        var period = savePeriod("Primer semestre", 1);
        var teacher = saveTeacher();
        var section = saveSection(period.getId(), teacher.getId(), "A");
        flushAndClear();

        var found = gateway.findSectionById(section.getId()).orElseThrow();
        assertEquals(section.getId(), found.getId());
        assertEquals("A", found.getName());
        assertEquals("2026-1", found.getPeriod());
        assertEquals("DOC-C5", found.getTeacherCode());
        assertNull(found.getRiskLevel());
    }

    @Test
    @DisplayName("un periodo ausente no elimina la seccion")
    void seccionSinPeriodo() {
        var section = saveSection(999_999L, saveTeacher().getId(), "SIN-PERIODO");
        flushAndClear();
        var found = gateway.findSectionById(section.getId()).orElseThrow();
        assertNull(found.getPeriod());
        assertEquals("DOC-C5", found.getTeacherCode());
    }

    @Test
    @DisplayName("un docente ausente no elimina la seccion")
    void seccionSinDocente() {
        var section = saveSection(savePeriod("2026-1", 1).getId(), 999_999L, "SIN-DOCENTE");
        flushAndClear();
        var found = gateway.findSectionById(section.getId()).orElseThrow();
        assertNull(found.getTeacherCode());
        assertEquals("2026-1", found.getPeriod());
    }

    @Test
    @DisplayName("una seccion inexistente devuelve vacio")
    void idInexistente() {
        assertTrue(gateway.findSectionById(999_999L).isEmpty());
    }

    @Test
    @DisplayName("sin puerto C3 las consultas de alertas no fallan")
    void sinPuertoDeAlertas() {
        var section = saveSection(savePeriod("2026-1", 1).getId(), saveTeacher().getId(), "A");
        flushAndClear();
        assertTrue(gateway.findAlerts(emptyFilter()).isEmpty());
        assertTrue(gateway.findAlertsBySectionId(section.getId()).isEmpty());
        assertTrue(gateway.findAlertsByCourseId(5L).isEmpty());
    }

    @Test
    @DisplayName("filtra por periodo con secciones de ambos semestres persistidas")
    void filtraPorPeriodo() {
        var teacher = saveTeacher();
        var first = saveSection(savePeriod("Primer semestre", 1).getId(), teacher.getId(), "A");
        saveSection(savePeriod("Segundo semestre", 7).getId(), teacher.getId(), "B");
        flushAndClear();
        var found = gateway.findSections(new ReportFilter("2026-1", null, null, null, null, null));
        assertEquals(1, found.size());
        assertEquals(first.getId(), found.getFirst().getId());
        assertEquals("2026-1", found.getFirst().getPeriod());
    }

    @Test
    @DisplayName("cuenta inscripciones reales solo de la seccion solicitada")
    void cuentaInscripcionesPorSeccion() {
        var period = savePeriod("2026-1", 1);
        var teacher = saveTeacher();
        var first = saveSection(period.getId(), teacher.getId(), "A");
        var second = saveSection(period.getId(), teacher.getId(), "B");
        enrollments.save(new Enrollment(null, first.getId(), 101L, LocalDate.of(2026, 1, 15), "ACTIVE"));
        enrollments.save(new Enrollment(null, first.getId(), 102L, LocalDate.of(2026, 1, 15), "INACTIVE"));
        enrollments.save(new Enrollment(null, second.getId(), 103L, LocalDate.of(2026, 1, 15), "ACTIVE"));
        flushAndClear();
        assertEquals(2, gateway.countEnrolledStudents(first.getId()));
        assertEquals(1, gateway.countEnrolledStudents(second.getId()));
        assertEquals(0, gateway.countEnrolledStudents(999_999L));
    }

    private AcademicPeriod savePeriod(String name, int startMonth) {
        return periods.save(new AcademicPeriod(name, 2026, LocalDate.of(2026, startMonth, 1),
                LocalDate.of(2026, startMonth + 5, 28), PeriodStatus.ACTIVE));
    }

    private Teacher saveTeacher() {
        return teachers.save(new Teacher("DOC-C5", "Docente de prueba C5", null, TeacherStatus.ACTIVE));
    }

    private Section saveSection(Long periodId, Long teacherId, String code) {
        return sections.save(new Section(null, periodId, 5L, teacherId, code, "ACTIVE"));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private ReportFilter emptyFilter() {
        return new ReportFilter(null, null, null, null, null, null);
    }
}
