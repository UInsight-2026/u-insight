package gt.edu.uinsight.imports.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Detalle de una fila de CSV rechazada durante la validación")
public class ImportErrorDetail {

    @Schema(description = "Número de fila del CSV donde ocurrió el error")
    private Integer fila;

    @Schema(description = "Campo que causó el rechazo")
    private String campo;

    @Schema(description = "Motivo del rechazo")
    private String motivo;

    public ImportErrorDetail() {
    }

    public ImportErrorDetail(Integer fila, String campo, String motivo) {
        this.fila = fila;
        this.campo = campo;
        this.motivo = motivo;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}

