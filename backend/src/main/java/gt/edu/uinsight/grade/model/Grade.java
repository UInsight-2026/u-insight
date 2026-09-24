package gt.edu.uinsight.grade.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "grades",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_grade_evaluation_student",
        columnNames = {"evaluation_id", "student_id"}
    )
)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluation_id", nullable = false)
    private Long evaluationId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "score", nullable = false, precision = 6, scale = 2)
    private BigDecimal score;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private GradeStatus status;

    public Grade() {}

    public Grade(Long evaluationId, Long studentId, BigDecimal score, LocalDateTime registeredAt, GradeStatus status) {
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        this.score = score;
        this.registeredAt = registeredAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public GradeStatus getStatus() { return status; }
    public void setStatus(GradeStatus status) { this.status = status; }
}
