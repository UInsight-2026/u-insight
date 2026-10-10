// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.dto.request;

import gt.edu.uinsight.evaluation.domain.EvaluationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * HU4: nuevo estado solicitado para la evaluación.
 * Transiciones válidas (RN6): DRAFT->ACTIVE, ACTIVE->CLOSED, DRAFT/ACTIVE->CANCELLED.
 */
@Schema(description = "Nuevo estado de la evaluación")
public class ChangeEvaluationStatusRequest {

    @Schema(description = "Estado destino", example = "ACTIVE",
            allowableValues = {"DRAFT", "ACTIVE", "CLOSED", "CANCELLED"})
    @NotBlank(message = "El nuevo estado (status) es obligatorio")
    @Pattern(regexp = EvaluationStatus.REGEX,
            message = "El estado debe ser DRAFT, ACTIVE, CLOSED o CANCELLED")
    private String status;

    public ChangeEvaluationStatusRequest() {
    }

    public ChangeEvaluationStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
