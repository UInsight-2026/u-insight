package gt.edu.uinsight.imports.dto.response;

import java.time.LocalDateTime;

import gt.edu.uinsight.imports.entity.EstadoImportacion;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta de GET /api/v1/imports/{id} (tarea 13, primer endpoint funcional).
 * Contrato base ya documentado en la Semana 1 (sección 6, API 3).
 */
@Schema(description = "Estado y resumen actual de una importación")
public class ImportStatusResponse {

    @Schema(description = "Identificador de la importación")
    private Long importId;

    @Schema(description = "Estado actual de la importación")
    private EstadoImportacion estado;

    @Schema(description = "Total de filas leídas del CSV")
    private Integer totalRegistros;

    @Schema(description = "Cantidad de registros válidos")
    private Integer registrosValidos;

    @Schema(description = "Cantidad de registros inválidos")
    private Integer registrosInvalidos;

    @Schema(description = "Fecha y hora de carga del archivo")
    private LocalDateTime fechaCarga;

    public ImportStatusResponse() {
    }

    public ImportStatusResponse(Long importId, EstadoImportacion estado,
                                 Integer totalRegistros, Integer registrosValidos,
                                 Integer registrosInvalidos, LocalDateTime fechaCarga) {
        this.importId = importId;
        this.estado = estado;
        this.totalRegistros = totalRegistros;
        this.registrosValidos = registrosValidos;
        this.registrosInvalidos = registrosInvalidos;
        this.fechaCarga = fechaCarga;
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

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }
}
