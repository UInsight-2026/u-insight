package gt.edu.uinsight.evaluation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EvaluationResponse {
    private Long id;
    private String name;
    private String description;
    private Long courseId;
    private Long sectionId;
    private BigDecimal maxScore;
    private LocalDateTime createdAt;

    public EvaluationResponse() {
    }

    public EvaluationResponse(Long id, String name, String description, Long courseId, Long sectionId, BigDecimal maxScore, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.courseId = courseId;
        this.sectionId = sectionId;
        this.maxScore = maxScore;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
