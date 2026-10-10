// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.dto.request;

import gt.edu.uinsight.evaluation.domain.EvaluationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** HU1: datos para registrar una evaluación. Siempre se crea en estado DRAFT. */
@Schema(description = "Datos para crear una evaluación")
public class CreateEvaluationRequest {

    @Schema(description = "Id de la sección (célula A4). Debe existir y estar ACTIVE", example = "1")
    @NotNull(message = "El ID de la sección es obligatorio")
    @Positive(message = "El ID de la sección debe ser positivo")
    private Long sectionId;

    @Schema(description = "Nombre de la evaluación", example = "Examen Parcial 1")
    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Schema(description = "Tipo de evaluación", example = "EXAM",
            allowableValues = {"EXAM", "QUIZ", "PROJECT", "LAB", "ASSIGNMENT", "OTHER"})
    @NotBlank(message = "El tipo de evaluación es obligatorio")
    @Pattern(regexp = EvaluationType.REGEX,
            message = "El tipo debe ser EXAM, QUIZ, PROJECT, LAB, ASSIGNMENT u OTHER")
    private String type;

    @Schema(description = "Fecha de la evaluación (yyyy-MM-dd)", example = "2026-10-15")
    @NotNull(message = "La fecha de la evaluación es obligatoria")
    private LocalDate evaluationDate;

    @Schema(description = "Nota máxima (RN2: mayor que 0)", example = "100")
    @NotNull(message = "La nota máxima es obligatoria")
    @DecimalMin(value = "0.01", message = "La nota máxima debe ser mayor a 0")
    @Digits(integer = 3, fraction = 2, message = "La nota máxima admite hasta 3 enteros y 2 decimales")
    private BigDecimal maximumScore;

    @Schema(description = "Ponderación en porcentaje (RN3: mayor que 0 y hasta 100)", example = "30")
    @NotNull(message = "La ponderación (weight) es obligatoria")
    @DecimalMin(value = "0.01", message = "La ponderación debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "La ponderación no puede ser mayor a 100")
    @Digits(integer = 3, fraction = 2, message = "La ponderación admite hasta 3 enteros y 2 decimales")
    private BigDecimal weight;

    public CreateEvaluationRequest() {
    }

    public CreateEvaluationRequest(Long sectionId, String name, String type, LocalDate evaluationDate,
                                   BigDecimal maximumScore, BigDecimal weight) {
        this.sectionId = sectionId;
        this.name = name;
        this.type = type;
        this.evaluationDate = evaluationDate;
        this.maximumScore = maximumScore;
        this.weight = weight;
    }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }

    public BigDecimal getMaximumScore() { return maximumScore; }
    public void setMaximumScore(BigDecimal maximumScore) { this.maximumScore = maximumScore; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
}
