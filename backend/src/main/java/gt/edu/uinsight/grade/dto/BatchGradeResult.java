package gt.edu.uinsight.grade.dto;

import java.util.List;

public class BatchGradeResult {

    private int totalRequested;
    private int totalRegistered;
    private int totalRejected;
    private List<BatchItemResult> results;

    public BatchGradeResult() {}

    public BatchGradeResult(int totalRequested, int totalRegistered, int totalRejected, List<BatchItemResult> results) {
        this.totalRequested = totalRequested;
        this.totalRegistered = totalRegistered;
        this.totalRejected = totalRejected;
        this.results = results;
    }

    public int getTotalRequested() { return totalRequested; }
    public int getTotalRegistered() { return totalRegistered; }
    public int getTotalRejected() { return totalRejected; }
    public List<BatchItemResult> getResults() { return results; }
}
