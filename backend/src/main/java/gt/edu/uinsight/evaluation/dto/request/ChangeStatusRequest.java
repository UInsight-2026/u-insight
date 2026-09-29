package gt.edu.uinsight.evaluation.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ChangeStatusRequest {

    @NotBlank(message = "El nuevo estado (status) es obligatorio")
    private String status;

    public ChangeStatusRequest() {
    }

    public ChangeStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
