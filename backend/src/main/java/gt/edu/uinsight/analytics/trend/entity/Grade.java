package gt.edu.uinsight.analytics.trend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "grade")
public class Grade {
    
    @Id 
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    
    private Long evaluationId;
    private Long studentId;
    private Double score;
    private String registeredAt;
    private String status;

    public Grade(Long evaluationId, Long studentId, Double score, String registeredAt, String status) {
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        this.score = score;
        this.registeredAt = registeredAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getEvaluationId() {
        return evaluationId;
    }

    public Double getScore() {
        return score;
    }
    public String getRegisteredAt() {
        return registeredAt;
    }
    public String getStatus() {
        return status;
    }
    public void setScore(Double score) {
        this.score = score;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }
    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

}
