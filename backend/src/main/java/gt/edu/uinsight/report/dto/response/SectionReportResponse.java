package gt.edu.uinsight.report.dto.response;

import java.util.List;

/**
 * Respuesta de GET /api/v1/reports/sections/{id}.
 *
 * Semana 4: expone los datos reales de periodo, docente y curso,
 * ademas del origen del riesgo y las fuentes no disponibles.
 */
public class SectionReportResponse {

    private final Long sectionId;
    private final String sectionName;
    private final String riskLevel;
    private final int studentsAtRisk;
    private final int activeAlerts;
    private final AnalyticsSnapshot analytics;

    private final String periodCode;
    private final String teacherCode;
    private final Long courseId;
    private final String riskSource;
    private final List<String> unavailableSources;

    public SectionReportResponse(
            Long sectionId,
            String sectionName,
            String riskLevel,
            int studentsAtRisk,
            int activeAlerts,
            AnalyticsSnapshot analytics,
            String periodCode,
            String teacherCode,
            Long courseId,
            String riskSource,
            List<String> unavailableSources) {

        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.riskLevel = riskLevel;
        this.studentsAtRisk = studentsAtRisk;
        this.activeAlerts = activeAlerts;
        this.analytics = analytics;
        this.periodCode = periodCode;
        this.teacherCode = teacherCode;
        this.courseId = courseId;
        this.riskSource = riskSource;
        this.unavailableSources = unavailableSources;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public int getStudentsAtRisk() {
        return studentsAtRisk;
    }

    public int getActiveAlerts() {
        return activeAlerts;
    }

    public AnalyticsSnapshot getAnalytics() {
        return analytics;
    }

    public String getPeriodCode() {
        return periodCode;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getRiskSource() {
        return riskSource;
    }

    public List<String> getUnavailableSources() {
        return unavailableSources;
    }
}