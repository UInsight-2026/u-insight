package gt.edu.uinsight.alert.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateAlertStatusRequest {
    
    @NotBlank(message = "El nuevo estado de la alerta es obligatorio")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}