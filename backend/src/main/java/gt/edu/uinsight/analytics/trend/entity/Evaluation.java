package gt.edu.uinsight.analytics.trend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table (name = "evaluation")
public class Evaluation {
    
    @Id 
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    
    private Long sectionId;
    private String name;
    private Type type;
    private String evaluationDate;
    private Double maximumScore;
    private Double weight;
    private Status status;

    public Evaluation(Long sectionId, String name, Type type, String evaluationDate, Double maximumScore, Double weight, Status status) {
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
    public Long getSectionId() {
        return sectionId;
    }
    public String getName() {
        return name;
    }
    public Type getType() {
        return type;
    }
    public String getEvaluationDate() {
        return evaluationDate;
    }
    public Double getMaximumScore() {
        return maximumScore;
    }
    public Double getWeight() {
        return weight;
    }
    public Status getStatus() {
        return status;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public void setEvaluationDate(String evaluationDate) {
        this.evaluationDate = evaluationDate;
    }
    public void setMaximumScore(Double maximumScore) {
        this.maximumScore = maximumScore;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }
    
}
