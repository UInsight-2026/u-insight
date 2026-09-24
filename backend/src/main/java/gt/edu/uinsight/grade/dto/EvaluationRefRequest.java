package gt.edu.uinsight.grade.dto;

import java.math.BigDecimal;

public class EvaluationRefRequest {

    /** Opcional: si se omite, se genera un id nuevo. Si se envia uno existente, se sobreescribe. */
    private Long id;
    private Long sectionId;
    private String name;
    private BigDecimal maximumScore;

    public EvaluationRefRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getMaximumScore() { return maximumScore; }
    public void setMaximumScore(BigDecimal maximumScore) { this.maximumScore = maximumScore; }
}
