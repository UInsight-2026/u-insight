package gt.edu.uinsight.grade.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchItemResult {

    private int index;
    private Long evaluationId;
    private Long studentId;
    private boolean success;
    private Long gradeId;
    private String error;

    public BatchItemResult() {}

    public static BatchItemResult success(int index, Long evaluationId, Long studentId, Long gradeId) {
        BatchItemResult result = new BatchItemResult();
        result.index = index;
        result.evaluationId = evaluationId;
        result.studentId = studentId;
        result.success = true;
        result.gradeId = gradeId;
        return result;
    }

    public static BatchItemResult failure(int index, Long evaluationId, Long studentId, String error) {
        BatchItemResult result = new BatchItemResult();
        result.index = index;
        result.evaluationId = evaluationId;
        result.studentId = studentId;
        result.success = false;
        result.error = error;
        return result;
    }

    public int getIndex() { return index; }
    public Long getEvaluationId() { return evaluationId; }
    public Long getStudentId() { return studentId; }
    public boolean isSuccess() { return success; }
    public Long getGradeId() { return gradeId; }
    public String getError() { return error; }
}
