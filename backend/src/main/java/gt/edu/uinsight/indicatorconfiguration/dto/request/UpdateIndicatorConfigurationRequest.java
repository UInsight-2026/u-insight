package gt.edu.uinsight.indicatorconfiguration.dto.request;

import jakarta.validation.constraints.Size;

public class UpdateIndicatorConfigurationRequest {

    @Size(max = 50, message = "El valor no puede superar 50 caracteres")
    private String value;

    @Size(max = 255, message = "La descripcion no puede superar 255 caracteres")
    private String description;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
