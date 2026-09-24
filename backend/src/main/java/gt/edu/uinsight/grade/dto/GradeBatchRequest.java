package gt.edu.uinsight.grade.dto;

import java.util.List;

public class GradeBatchRequest {

    private List<GradeRequest> grades;

    public GradeBatchRequest() {}

    public List<GradeRequest> getGrades() { return grades; }
    public void setGrades(List<GradeRequest> grades) { this.grades = grades; }
}
