package gt.edu.uinsight.grade.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Copia de referencia de una evaluacion, minima para validar el registro de
 * calificaciones (existencia, seccion y nota maxima). La tabla se nombra
 * "grade_evaluation_ref" -- separada de la tabla "evaluations" que
 * implementara la celula A5 -- para no chocar con su esquema real.
 * Cuando A5 este integrada, este modulo deberia consultarla a ella en vez
 * de mantener esta copia.
 */
@Entity
@Table(name = "grade_evaluation_ref")
public class EvaluationRef {

    @Id
    private Long id;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "maximum_score", nullable = false, precision = 6, scale = 2)
    private BigDecimal maximumScore;

    public EvaluationRef() {}

    public EvaluationRef(Long id, Long sectionId, String name, BigDecimal maximumScore) {
        this.id = id;
        this.sectionId = sectionId;
        this.name = name;
        this.maximumScore = maximumScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getMaximumScore() { return maximumScore; }
    public void setMaximumScore(BigDecimal maximumScore) { this.maximumScore = maximumScore; }
}
