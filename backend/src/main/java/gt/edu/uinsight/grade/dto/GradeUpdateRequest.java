package gt.edu.uinsight.grade.dto;

import java.math.BigDecimal;

public class GradeUpdateRequest {

    private BigDecimal score;

    public GradeUpdateRequest() {}

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
}
