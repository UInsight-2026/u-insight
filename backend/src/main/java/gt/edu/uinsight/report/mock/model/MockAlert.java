package gt.edu.uinsight.report.mock.model;

import java.util.Set;

/**
 * Simula una alerta tal como la entregarian las Celulas C2 (reglas) y
 * C3 (ciclo de vida de alertas).
 *
 * NOTA: el ciclo de vida oficial es NEW -> UNDER_REVIEW -> IN_PROGRESS
 * -> RESOLVED (o DISMISSED). Falta confirmar con C2/C3 si el filtro
 * publico usa esos valores o "ACTIVE".
 */
public class MockAlert {

    private static final Set<String> CLOSED_STATUSES = Set.of("RESOLVED", "DISMISSED");

    private final Long id;
    private final Long sectionId;
    private final String sectionName;
    private final Long courseId;
    private final String courseCode;
    private final String teacherCode;
    private final String period;
    private final String type; // PERFORMANCE | TREND | DISPERSION
    private final String riskLevel; // LOW | MEDIUM | HIGH
    private final String status; // NEW | UNDER_REVIEW | IN_PROGRESS | RESOLVED | DISMISSED
    private final String title;
    private final String generatedAt;

    public MockAlert(Long id, Long sectionId, String sectionName, Long courseId, String courseCode,
                      String teacherCode, String period, String type, String riskLevel,
                      String status, String title, String generatedAt) {
        this.id = id;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.teacherCode = teacherCode;
        this.period = period;
        this.type = type;
        this.riskLevel = riskLevel;
        this.status = status;
        this.title = title;
        this.generatedAt = generatedAt;
    }

    public boolean isActive() {
        return !CLOSED_STATUSES.contains(status.toUpperCase());
    }

    public Long getId() {
        return id;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public String getPeriod() {
        return period;
    }

    public String getType() {
        return type;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }
}
