package gt.edu.uinsight.analytics.trend.entity;

import java.util.List;

import gt.edu.uinsight.analytics.trend.dto.response.TrendPoint;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

// Parche de arranque aportado por C7: esta entidad tumbaba el contexto de Spring. 'points'
// es una List de un DTO, que JPA no sabe persistir, y faltaba el constructor sin argumentos
// que Hibernate necesita. Ni la entidad ni TrendRepository se usan todavia en ningun
// servicio, asi que se marca 'points' como @Transient en lugar de disenar la tabla.
// Pendiente: la celula B4 decide si la entidad se persiste de verdad o se elimina.
@Entity
@Table(name = "trends")
public class Trend {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TrendClassification classification;

    @Column(name = "average_change")
    private double averageChange;

    @Transient
    private List<TrendPoint> points;

    protected Trend() {
    }

    public Trend(TrendClassification classification, double averageChange, List<TrendPoint> points) {
        this.classification = classification;
        this.averageChange = averageChange;
        this.points = points;
    }

    public Long getId() {
        return id;
    }
    public TrendClassification getClassification() {
        return classification;
    }
    public double getAverageChange() {
        return averageChange;
    }
    public List<TrendPoint> getPoints() {
        return points;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setClassification(TrendClassification classification) {
        this.classification = classification;
    }
    public void setAverageChange(double averageChange) {
        this.averageChange = averageChange;
    }
    public void setPoints(List<TrendPoint> points) {
        this.points = points;
    }
}
