package gt.edu.uinsight.grade.dto;

import gt.edu.uinsight.grade.model.Grade;
import gt.edu.uinsight.grade.model.GradeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GradeResponse {

    private Long id;
    private Long evaluationId;
    private Long studentId;
    private BigDecimal score;
    private LocalDateTime registeredAt;
    private GradeStatus status;

    public GradeResponse() {}

    public static GradeResponse fromEntity(Grade grade) {
        GradeResponse response = new GradeResponse();
        response.id = grade.getId();
        response.evaluationId = grade.getEvaluationId();
        response.studentId = grade.getStudentId();
        response.score = grade.getScore();
        response.registeredAt = grade.getRegisteredAt();
        response.status = grade.getStatus();
        return response;
    }

    public Long getId() { return id; }
    public Long getEvaluationId() { return evaluationId; }
    public Long getStudentId() { return studentId; }
    public BigDecimal getScore() { return score; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public GradeStatus getStatus() { return status; }
}
