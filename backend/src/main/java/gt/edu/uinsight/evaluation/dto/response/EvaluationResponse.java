package gt.edu.uinsight.evaluation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EvaluationResponse {
    
    private Long id;
    private Long sectionId;
    private String name;
    private String type;
    private LocalDate evaluationDate;
    private BigDecimal maximumScore;
    private BigDecimal weight;
    private String status;

    public EvaluationResponse() {
    }

    public EvaluationResponse(Long id, Long sectionId, String name, String type, LocalDate evaluationDate, BigDecimal maximumScore, BigDecimal weight, String status) {
        this.id = id;
        this.sectionId = sectionId;
        this.name = name;
        this.type = type;
        this.evaluationDate = evaluationDate;
        this.maximumScore = maximumScore;
        this.weight = weight;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public BigDecimal getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(BigDecimal maximumScore) {
        this.maximumScore = maximumScore;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}