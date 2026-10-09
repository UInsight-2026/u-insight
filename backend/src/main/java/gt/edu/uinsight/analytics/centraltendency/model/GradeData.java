package gt.edu.uinsight.analytics.centraltendency.model;

import java.math.BigDecimal;

public class GradeData {
    private Long gradeId;
    private Long evaluationId;
    private Long studentId;
    private Long sectionId;
    private Long courseId;
    private BigDecimal score;

    public GradeData() {
    }

    public GradeData(Long gradeId, Long evaluationId, Long studentId,
                     Long sectionId, Long courseId, BigDecimal score) {
        this.gradeId = gradeId;
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.score = score;
    }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
}
