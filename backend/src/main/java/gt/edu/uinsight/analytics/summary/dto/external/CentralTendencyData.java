package gt.edu.uinsight.analytics.summary.dto.external;

import java.util.List;

public class CentralTendencyData {
    private Long sectionId;
    private Integer sampleSize;
    private Double mean;
    private Double median;
    private List<Double> mode;

    public Long getSectionId(){return this.sectionId;}
    public void setSectionId(Long id){ this.sectionId = id;}

    public Integer getSampleSize(){ return this.sampleSize;}
    public void setSampleSize(Integer sampleSize){ this.sampleSize = sampleSize;}

    public Double getMean(){ return this.mean;}
    public void setMean(Double mean){ this.mean = mean;}

    public Double getMedian(){ return this.median;}
    public void setMedian(Double median){ this.median = median;}

    public List<Double> getMode(){return mode;}
    public void setMode(List<Double> mode){ this.mode = mode;}
}
