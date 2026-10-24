package gt.edu.uinsight.analytics.summary.entity;

import java.util.List;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultados analíticos consolidados para una sección. Los componentes no disponibles se indican en unavailableComponents.")
public class SectionSummary {

    private Long sectionId;
    private CentralTendencyResponse centralTendency;
    private SectionPositionResponse position;
    private DispersionResponse dispersion;
    private TrendResponse trend;
    private StudentComparisonResponse studentComparison;
    private Integer studentsAtRisk;
    private Double studentsAtRiskPercentage;
    private Integer studentsAssessed;
    private Integer studentsEnrolled;
    private List<String> unavailableComponents;

    @Schema(description = "Identificador de la sección", example = "10")
    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    @Schema(description = "Resultados de tendencia central calculados por B1")
    public CentralTendencyResponse getCentralTendencyData() { return centralTendency; }
    public void setCentralTendencyData(CentralTendencyResponse ct) { this.centralTendency = ct; }

    @Schema(description = "Cuartiles y percentiles calculados por B2")
    public SectionPositionResponse getPositionData() { return position; }
    public void setPositionData(SectionPositionResponse position) { this.position = position; }

    @Schema(description = "Métricas de dispersión calculadas por B3")
    public DispersionResponse getDispersionData() { return dispersion; }
    public void setDispersionData(DispersionResponse dd) { this.dispersion = dd; }

    @Schema(description = "Clasificación y puntos de evolución calculados por B4")
    public TrendResponse getTrendData() { return trend; }
    public void setTrendData(TrendResponse td) { this.trend = td; }

    @Schema(description = "Comparación individual disponible en el resumen, si aplica")
    public StudentComparisonResponse getStudentComparisonData() { return studentComparison; }
    public void setStudentComparisonData(StudentComparisonResponse scd) { this.studentComparison = scd; }

    @Schema(description = "Estudiantes activos con promedio de calificaciones normalizado menor al umbral de bajo rendimiento (60 por defecto)", example = "3")
    public Integer getStudentsAtRisk() { return studentsAtRisk; }
    public void setStudentsAtRisk(Integer sar) { this.studentsAtRisk = sar; }

    @Schema(description = "Porcentaje en riesgo entre estudiantes activos con al menos una calificación", example = "25.0")
    public Double getStudentsAtRiskPercentage() { return studentsAtRiskPercentage; }
    public void setStudentsAtRiskPercentage(Double percentage) { this.studentsAtRiskPercentage = percentage; }

    @Schema(description = "Estudiantes activos con al menos una calificación", example = "8")
    public Integer getStudentsAssessed() { return studentsAssessed; }
    public void setStudentsAssessed(Integer studentsAssessed) { this.studentsAssessed = studentsAssessed; }

    @Schema(description = "Total de estudiantes con matrícula activa en la sección", example = "10")
    public Integer getStudentsEnrolled() { return studentsEnrolled; }
    public void setStudentsEnrolled(Integer studentsEnrolled) { this.studentsEnrolled = studentsEnrolled; }

    @Schema(description = "Nombres de los componentes que no pudieron obtenerse; vacío cuando el resumen está completo",
            example = "[\"dispersion\"]")
    public List<String> getUnavailableComponents() { return unavailableComponents; }
    public void setUnavailableComponents(List<String> unavailable) { this.unavailableComponents = unavailable; }
}