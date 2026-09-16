package gt.edu.uinsight.imports.dto.response;

import gt.edu.uinsight.imports.entity.EstadoImportacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema(description = "Resultado del registro/validación inicial de un archivo CSV")
public class ImportValidateResponse {

    @Schema(description = "Identificador de la importación creada")
    private Long importId;

    @Schema(description = "Estado actual de la importación")
    private EstadoImportacion estado;

    @Schema(description = "Total de filas leídas del CSV")
    private Integer totalRegistros;

    @Schema(description = "Cantidad de registros válidos")
    private Integer registrosValidos;

    @Schema(description = "Cantidad de registros inválidos")
    private Integer registrosInvalidos;

    @Schema(description = "Detalle de filas rechazadas")
    private List<ImportErrorDetail> errores;

    public ImportValidateResponse() {
    }

    public ImportValidateResponse(Long importId, EstadoImportacion estado,
                                   Integer totalRegistros, Integer registrosValidos,
                                   Integer registrosInvalidos,
                                   List<ImportErrorDetail> errores) {
        this.importId = importId;
        this.estado = estado;
        this.totalRegistros = totalRegistros;
        this.registrosValidos = registrosValidos;
        this.registrosInvalidos = registrosInvalidos;
        this.errores = errores;
    }

    public Long getImportId() {
        return importId;
    }

    public void setImportId(Long importId) {
        this.importId = importId;
    }

    public EstadoImportacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoImportacion estado) {
        this.estado = estado;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Integer getRegistrosValidos() {
        return registrosValidos;
    }

    public void setRegistrosValidos(Integer registrosValidos) {
        this.registrosValidos = registrosValidos;
    }

    public Integer getRegistrosInvalidos() {
        return registrosInvalidos;
    }

    public void setRegistrosInvalidos(Integer registrosInvalidos) {
        this.registrosInvalidos = registrosInvalidos;
    }

    public List<ImportErrorDetail> getErrores() {
        return errores;
    }

    public void setErrores(List<ImportErrorDetail> errores) {
        this.errores = errores;
    }
}
