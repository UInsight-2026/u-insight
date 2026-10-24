package gt.edu.uinsight.analytics.position.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta de GET /api/v1/analytics/students/{id}/position
 *
 * Indica en que percentil se ubica el estudiante respecto al resto de su seccion.
 */
@Schema(description = "Posición de un estudiante respecto a las calificaciones de su sección")
public class StudentPositionResponse {

    @Schema(description = "Código institucional del estudiante", example = "EST-0001")
    private String studentCode;
    @Schema(description = "Promedio de calificaciones del estudiante", example = "85.5")
    private double studentAverage;
    @Schema(description = "Percentil del estudiante dentro de la sección (0 a 100)", example = "72")
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
