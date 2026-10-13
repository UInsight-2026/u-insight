package gt.edu.uinsight.analytics.summary.dto.external;

public class DispersionData {
    private Double min;
    private Double max;
    private Double range;
    private Double variance;
    private Double standarDeviation;

    public Double getMin(){ return this.min;}
    public void setMin(Double min){this.min = min;}

    public Double getMax(){ return this.max;}
    public void setMax(Double max){this.max = max;}

    public Double getRange(){ return this.range;}
    public void setRange(Double range){this.range = range;}

    public Double getVariance(){ return this.variance;}
    public void setVariance(Double variance){this.variance = variance;}

    public Double getStandarDeviation(){ return this.standarDeviation;}
    public void setStandarDeviation(Double standarDeviation){this.standarDeviation = standarDeviation;}
}
