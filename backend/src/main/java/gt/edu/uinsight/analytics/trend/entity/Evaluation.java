package gt.edu.uinsight.analytics.trend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Parche de arranque aportado por C7: las celulas A5 y B3 tienen tambien una entidad
// llamada Evaluation sobre esta misma tabla. Hibernate identifica las entidades por su
// nombre simple, asi que las tres colisionaban al construir el modelo. Se le da un nombre
// de entidad distinto, se mantiene el mismo @Table y se declaran los @Column en snake_case
// igual que A5 y B3: sin eso Hibernate rechazaba la tabla 'evaluation' por tener una misma
// columna fisica referida con dos nombres logicos distintos.
@Entity(name = "TrendEvaluation")
@Table (name = "evaluation")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_id")
    private Long sectionId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "evaluation_date")
    private String evaluationDate;

    @Column(name = "maximum_score")
    private Double maximumScore;

    private Double weight;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Constructor sin argumentos agregado por C7: Hibernate lo necesita para instanciar
    // la entidad y sin el el contexto de Spring no arranca.
    protected Evaluation() {
    }

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
