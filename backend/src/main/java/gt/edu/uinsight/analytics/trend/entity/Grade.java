package gt.edu.uinsight.analytics.trend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Vista de solo lectura de una calificación para el cálculo de tendencias. */
@Entity(name = "TrendGrade")
@Table(name = "grade")
public class Grade {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluation_id")
    private Long evaluationId;

    @Column(name = "student_id")
    private Long studentId;

    private Double score;

    @Column(name = "registered_at")
    private String registeredAt;

    private String status;

    // Constructor sin argumentos agregado por C7: Hibernate lo necesita para instanciar
    // la entidad y sin el el contexto de Spring no arranca.
    protected Grade() {
    }

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

    public Long getEvaluationId() {
        return evaluationId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
