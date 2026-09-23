package gt.edu.uinsight.analytics.trend.entity;

import java.util.List;

import gt.edu.uinsight.analytics.trend.dto.response.TrendPoint;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "trends")
public class Trend {
    @Id 
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private TrendClassification classification;
    private double averageChange;
    private List<TrendPoint> points;

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
