package gt.edu.uinsight.imports.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Cabecera de una carga de calificaciones por CSV.
 * Entidad diseñada por Adolfo Gonzales (célula A7, sección 5 de la cédula).
 *
 * Sigue la convención del esquema maestro: llave primaria BIGINT autoincremental
 * y relaciones expresadas como columnas FK planas (sin @ManyToOne), igual que el
 * resto de tablas del proyecto.
 */
@Entity
@Table(name = "importacion")
public class Importacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime fechaCarga;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoImportacion estado;

    @Column(name = "total_registros", nullable = false)
    private Integer totalRegistros;

    @Column(name = "registros_validos", nullable = false)
    private Integer registrosValidos;

    @Column(name = "registros_invalidos", nullable = false)
    private Integer registrosInvalidos;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    protected Importacion() {
        // constructor requerido por JPA
    }

    public Importacion(String nombreArchivo, LocalDateTime fechaCarga,
                        EstadoImportacion estado, Long usuarioId) {
        this.nombreArchivo = nombreArchivo;
        this.fechaCarga = fechaCarga;
        this.estado = estado;
        this.usuarioId = usuarioId;
        this.totalRegistros = 0;
        this.registrosValidos = 0;
        this.registrosInvalidos = 0;
    }

    public Long getId() {
        return id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
