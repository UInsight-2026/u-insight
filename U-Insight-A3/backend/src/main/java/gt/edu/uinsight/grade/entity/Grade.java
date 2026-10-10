package gt.edu.uinsight.grade.entity;

import gt.edu.uinsight.student.entity.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Contrato de persistencia compartido con A6; A3 lo consulta para el historial.
 */
@Entity
@Table(name = "grades", uniqueConstraints = {
        @UniqueConstraint(name = "uq_grade_student_evaluation",
                columnNames = {"student_id", "evaluation_id"})
})
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluation_id", nullable = false)
    private Long evaluationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_grade_student"))
    private Student student;

    @Column(name = "score", nullable = false, precision = 8, scale = 2)
    private BigDecimal score;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    protected Grade() {
        // Requerido por JPA.
    }

    public Grade(Long evaluationId, Student student, BigDecimal score, String status) {
        this.evaluationId = evaluationId;
        this.student = student;
        this.score = score;
        this.status = status == null ? "ACTIVE" : status;
    }

    @PrePersist
    protected void onCreate() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEvaluationId() {
        return evaluationId;
    }

    public Student getStudent() {
        return student;
    }

    public BigDecimal getScore() {
        return score;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public String getStatus() {
        return status;
    }
}