package gt.edu.uinsight.analytics.position.dto.response;

/**
 * Respuesta de GET /api/v1/analytics/students/{id}/position
 *
 * Indica en que percentil se ubica el estudiante respecto al resto de su seccion.
 */
public class StudentPositionResponse {

    private String studentCode;
    private double studentAverage;
    private int percentile;

    public StudentPositionResponse(String studentCode, double studentAverage, int percentile) {
        this.studentCode = studentCode;
        this.studentAverage = studentAverage;
        this.percentile = percentile;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public double getStudentAverage() {
        return studentAverage;
    }

    public int getPercentile() {
        return percentile;
    }
}
