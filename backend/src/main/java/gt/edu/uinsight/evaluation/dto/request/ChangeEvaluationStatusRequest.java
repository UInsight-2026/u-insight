// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * HU4: nuevo estado solicitado para la evaluación.
 * Transiciones válidas (RN6): DRAFT->ACTIVE, ACTIVE->CLOSED, DRAFT/ACTIVE->CANCELLED.
 */
public class ChangeEvaluationStatusRequest {

    @NotBlank(message = "El nuevo estado (status) es obligatorio")
    private String status;

    public ChangeEvaluationStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
