package gt.edu.uinsight.alertrule.dto.request;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nuevo estado de activación de una regla")
public class UpdateStatusRequest {

    @Schema(description = "true para activar la regla; false para desactivarla", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El atributo active no puede ser nulo")
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}