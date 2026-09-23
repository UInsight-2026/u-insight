package gt.edu.uinsight.analytics.summary.dto.external;

import java.util.List;

public class TrendData {
    private String classification;
    private Double averageChange;
    private List<Point> points;

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public Double getAverageChange() { return averageChange; }
    public void setAverageChange(Double averageChange) { this.averageChange = averageChange; }

    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }

    public static class Point {
        private String label;
        private Double value;
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }
}
