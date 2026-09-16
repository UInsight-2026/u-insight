package gt.edu.uinsight.report.dto.response;

/**
 * Representa una alerta dentro del listado de GET /api/v1/reports/alerts.
 * (Integrante 3 - Allan).
 */
public class AlertItemResponse {

    private final Long id;
    private final Long sectionId;
    private final String courseCode;
    private final String type;
    private final String riskLevel;
    private final String status;
    private final String title;
    private final String generatedAt;

    public AlertItemResponse(Long id, Long sectionId, String courseCode, String type,
                              String riskLevel, String status, String title, String generatedAt) {
        this.id = id;
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.type = type;
        this.riskLevel = riskLevel;
        this.status = status;
        this.title = title;
        this.generatedAt = generatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public String getCourseCode() {
        return courseCode;
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
