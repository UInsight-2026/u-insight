package gt.edu.uinsight.analytics.summary.dto.external;

import java.util.Map;

public class PositionData {
    private Long sectionId;
    private Integer sampleSize;
    private Map<String, Double> percentiles;
    private Quartiles quartiles;

    public Long getSectionId(){ return this.sectionId;}
    public void setSectionId(Long sectionId){ this.sectionId = sectionId;}

    public Integer getSampleSize(){ return this.sampleSize;}
    public void setSampleSize(Integer sampleSize){this.sampleSize = sampleSize;}

    public Map<String, Double> getPercentiles(){ return this.percentiles;}
    public void setPercentiles(Map<String, Double> percentiles){ this.percentiles = percentiles;}

    public Quartiles getQuartiles(){ return this.quartiles;}
    public void setQuartiles(Quartiles quartiles){ this.quartiles = quartiles;}

    public static class Quartiles{
        private Double q1;
        private Double q2;
        private Double q3;
        private Double q4;

        public Double getQ1(){ return this.q1;}
        public void setQ1(Double q1){this.q1 = q1;}

        public Double getQ2(){return this.q2;}
        public void setQ2(Double q2){this.q2 = q2;}

        public Double getQ3(){return this.q3;}
        public void setQ3(Double q3){this.q3 = q3;}

        public Double getQ4(){return this.q4;}
        public void setQ4(Double q4){this.q4 = q4;}
    }
}
