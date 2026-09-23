package gt.edu.uinsight.evaluation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateEvaluationRequest {

    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotNull(message = "La fecha de la evaluación es obligatoria")
    private LocalDate evaluationDate;

    @NotNull(message = "La nota máxima es obligatoria")
    @DecimalMin(value = "0.01", message = "La nota máxima debe ser mayor a 0")
    private BigDecimal maximumScore;

    @NotNull(message = "La ponderación (weight) es obligatoria")
    @DecimalMin(value = "0.01", message = "La ponderación debe ser mayor a 0")
    private BigDecimal weight;

    public UpdateEvaluationRequest() {
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public BigDecimal getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(BigDecimal maximumScore) {
        this.maximumScore = maximumScore;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}