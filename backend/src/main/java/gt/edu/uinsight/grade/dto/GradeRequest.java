package gt.edu.uinsight.grade.dto;

import java.math.BigDecimal;

public class GradeRequest {

    private Long evaluationId;
    private Long studentId;
    private BigDecimal score;

    public GradeRequest() {}

    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
}
