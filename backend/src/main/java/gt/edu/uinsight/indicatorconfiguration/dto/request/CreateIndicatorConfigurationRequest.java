package gt.edu.uinsight.indicatorconfiguration.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateIndicatorConfigurationRequest {

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 80, message = "La clave no puede superar 80 caracteres")
    @Pattern(
            regexp = "^[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*$",
            message = "La clave debe usar mayusculas y guion bajo, por ejemplo HIGH_RISK_PERCENTAGE"
    )
    private String key;

    @NotBlank(message = "El valor es obligatorio")
    @Size(max = 50, message = "El valor no puede superar 50 caracteres")
    private String value;

    @Size(max = 255, message = "La descripcion no puede superar 255 caracteres")
    private String description;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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
