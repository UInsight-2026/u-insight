package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import gt.edu.uinsight.academicperiod.repository.AcademicPeriodRepository;
import gt.edu.uinsight.enrollment.repository.EnrollmentRepository;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockCourse;
import gt.edu.uinsight.report.mock.model.MockSection;
import gt.edu.uinsight.section.entity.Section;
import gt.edu.uinsight.section.repository.SectionRepository;
import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Traduce los catalogos A4/A1/A2 al contrato de lectura de C5 sin cambiar los
 * servicios. Cada fuente falla por separado: no se inventan cursos ni riesgo.
 * Las alertas admiten ausencia del puerto hasta integrar la entrega de Allan.
 * No guarda estado de disponibilidad entre peticiones concurrentes.
 */
@Component
@ConditionalOnProperty(name = "c5.data-source", havingValue = "jpa")
public class JpaReportDataGateway implements ReportDataGateway {
    private static final Logger log = LoggerFactory.getLogger(JpaReportDataGateway.class);
    private final SectionRepository sectionRepository;
    private final AcademicPeriodRepository periodRepository;
    private final TeacherRepository teacherRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RiskGateway riskGateway;
    private final AlertQueryPort alertQueryPort;

    public JpaReportDataGateway(SectionRepository sectionRepository,
                                AcademicPeriodRepository periodRepository,
                                TeacherRepository teacherRepository,
                                EnrollmentRepository enrollmentRepository,
                                RiskGateway riskGateway,
                                @Nullable AlertQueryPort alertQueryPort) {
        this.sectionRepository = sectionRepository;
        this.periodRepository = periodRepository;
        this.teacherRepository = teacherRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.riskGateway = riskGateway;
        this.alertQueryPort = alertQueryPort;
    }

    @Override
    public List<MockSection> findSections(ReportFilter filter) {
        return querySections(filter).stream().map(this::toReadModel)
                .filter(section -> matchesSection(section, filter)).toList();
    }

    @Override
    public Optional<MockSection> findSectionById(Long id) {
        return read("A4", () -> sectionRepository.findById(id), Optional.<Section>empty())
                .map(this::toReadModel);
    }

    @Override
    public List<MockSection> findSectionsByCourseId(Long courseId) {
        return read("A4", () -> sectionRepository.findByCourseId(courseId), List.<Section>of())
                .stream().map(this::toReadModel).toList();
    }

    private List<Section> querySections(ReportFilter filter) {
        if (filter != null && hasText(filter.getPeriod())) {
            // Un codigo puede corresponder a varios periodos con nombres libres.
            List<Long> ids = read("A1", periodRepository::findAll, List.<AcademicPeriod>of())
                    .stream().filter(p -> matches(filter.getPeriod(), periodCode(p)))
                    .map(AcademicPeriod::getId).toList();
            return ids.stream().flatMap(id -> read("A4",
                    () -> sectionRepository.findByAcademicPeriodId(id), List.<Section>of()).stream()).toList();
        }
        if (filter != null && hasText(filter.getTeacher())) {
            return read("A2", () -> teacherRepository.findByTeacherCodeIgnoreCase(filter.getTeacher()),
                    Optional.<Teacher>empty())
                    .map(t -> read("A4", () -> sectionRepository.findByTeacherId(t.getId()), List.<Section>of()))
                    .orElseGet(List::of);
        }
        return read("A4", sectionRepository::findAll, List.of());
    }

    private MockSection toReadModel(Section section) {
        String teacherCode = section.getTeacherId() == null ? null
                : read("A2", () -> teacherRepository.findById(section.getTeacherId()), Optional.<Teacher>empty())
                        .map(Teacher::getTeacherCode).orElse(null);
        String period = section.getAcademicPeriodId() == null ? null
                : read("A1", () -> periodRepository.findById(section.getAcademicPeriodId()), Optional.<AcademicPeriod>empty())
                        .map(this::periodCode).orElse(null);
        var risk = read("B7", () -> riskGateway.getSectionRisk(section.getId()), null);
        return new MockSection(section.getId(), section.getSectionCode(), section.getCourseId(),
                null, teacherCode, period,
                risk != null && risk.isAvailable() ? risk.getRiskLevel() : null, 0);
    }

    private boolean matchesSection(MockSection section, ReportFilter filter) {
        return filter == null || (matches(filter.getPeriod(), section.getPeriod())
                && matches(filter.getTeacher(), section.getTeacherCode())
                && matches(filter.getCourse(), section.getCourseCode())
                && matches(filter.getSection(), section.getName())
                && matches(filter.getRiskLevel(), section.getRiskLevel()));
    }

    /** A1 aun no publica Course en develop: no se sustituye por datos mock. */
    @Override
    public List<MockCourse> findCourses(ReportFilter filter) {
        log.warn("INTEGRATION_UNAVAILABLE source=A1 reason=sin catalogo de cursos");
        return List.of();
    }

    @Override
    public Optional<MockCourse> findCourseById(Long id) {
        log.warn("INTEGRATION_UNAVAILABLE source=A1 courseId={} reason=sin catalogo de cursos", id);
        return Optional.empty();
    }

    @Override
    public List<MockAlert> findAlerts(ReportFilter filter) {
        return enrichAlerts(readAlerts(() -> alertQueryPort.findAll())).stream()
                .filter(a -> filter == null || (matches(filter.getPeriod(), a.getPeriod())
                        && matches(filter.getTeacher(), a.getTeacherCode())
                        && matches(filter.getCourse(), a.getCourseCode())
                        && matches(filter.getSection(), a.getSectionName())
                        && matches(filter.getRiskLevel(), a.getRiskLevel())
                        && matches(filter.getAlertStatus(), a.getStatus())))
                .toList();
    }

    @Override
    public List<MockAlert> findAlertsBySectionId(Long sectionId) {
        return enrichAlerts(readAlerts(() -> alertQueryPort.findBySectionId(sectionId)));
    }

    @Override
    public List<MockAlert> findAlertsByCourseId(Long courseId) {
        List<Long> sectionIds = read("A4", () -> sectionRepository.findByCourseId(courseId), List.<Section>of())
                .stream().map(Section::getId).toList();
        if (sectionIds.isEmpty()) {
            return List.of();
        }
        return enrichAlerts(readAlerts(() -> alertQueryPort.findAll()).stream()
                .filter(a -> sectionIds.contains(a.getSectionId())).toList());
    }

    private List<MockAlert> readAlerts(Supplier<List<MockAlert>> query) {
        if (alertQueryPort == null) {
            log.warn("INTEGRATION_UNAVAILABLE source=C3 reason=puerto de alertas pendiente");
            return List.of();
        }
        return read("C3", query, List.of());
    }

    /** C3 solo aporta sectionId; los filtros necesitan los catalogos de esa seccion. */
    private List<MockAlert> enrichAlerts(List<MockAlert> alerts) {
        Map<Long, Optional<MockSection>> sections = new HashMap<>();
        return alerts.stream().map(a -> {
            MockSection section = a.getSectionId() == null ? null
                    : sections.computeIfAbsent(a.getSectionId(), this::findSectionById).orElse(null);
            return new MockAlert(a.getId(), a.getSectionId(), section == null ? null : section.getName(),
                    section == null ? null : section.getCourseId(), section == null ? null : section.getCourseCode(),
                    section == null ? null : section.getTeacherCode(), section == null ? null : section.getPeriod(),
                    a.getType(), section == null ? null : section.getRiskLevel(),
                    a.getStatus(), a.getTitle(), a.getGeneratedAt());
        }).toList();
    }

    @Override
    public int countEnrolledStudents(Long sectionId) {
        return read("A4", () -> enrollmentRepository.findBySectionId(sectionId).size(), 0);
    }

    /** El semestre se deduce de startDate si A1 usa un nombre libre. */
    private String periodCode(AcademicPeriod period) {
        if (period.getName() != null && period.getName().matches("\\d{4}-[12]")) {
            return period.getName();
        }
        if (period.getYear() == null || period.getStartDate() == null) {
            return null;
        }
        return period.getYear() + "-" + (period.getStartDate().getMonthValue() <= 6 ? 1 : 2);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean matches(String filter, String value) {
        return !hasText(filter) || filter.equalsIgnoreCase(value);
    }

    private <T> T read(String source, Supplier<T> query, T fallback) {
        try {
            T result = query.get();
            if (result != null) {
                return result;
            }
            log.warn("INTEGRATION_ERROR source={} reason=respuesta nula", source);
        } catch (RuntimeException ex) {
            log.warn("INTEGRATION_ERROR source={} message={}", source, ex.getMessage());
        }
        return fallback;
    }
}
