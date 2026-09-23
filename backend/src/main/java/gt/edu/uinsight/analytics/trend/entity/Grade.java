package gt.edu.uinsight.analytics.trend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Parche de arranque aportado por C7: la celula B3 (dispersion) tiene tambien una entidad
// llamada Grade sobre esta misma tabla. Hibernate identifica las entidades por su nombre
// simple y ambas colisionaban. Se le da un nombre de entidad distinto, se mantiene el mismo
// @Table y se declaran los @Column en snake_case igual que B3, porque Hibernate rechaza que
// una misma columna fisica se refiera con dos nombres logicos distintos.
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
