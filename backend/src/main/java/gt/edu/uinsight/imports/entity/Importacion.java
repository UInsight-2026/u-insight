package gt.edu.uinsight.imports.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Entity
@Table(name = "importacion")
public class Importacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime fechaCarga;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoImportacion estado;

    @Column(name = "total_registros")
    private Integer totalRegistros = 0;

    @Column(name = "registros_validos")
    private Integer registrosValidos = 0;

    @Column(name = "registros_invalidos")
    private Integer registrosInvalidos = 0;

    @Column(name = "usuario_id")
    private Long usuarioId;

    // ---- Control propio de A7: archivo original y trazabilidad ----

    @Lob
    @Column(name = "contenido_archivo", columnDefinition = "LONGBLOB")
    private byte[] contenidoArchivo;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "hash_sha256", length = 64)
    private String hashSha256;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "notas_enviadas")
    private Integer notasEnviadas;

    @Column(name = "notas_registradas")
    private Integer notasRegistradas;

    protected Importacion() {
    }

    public Importacion(String nombreArchivo, LocalDateTime fechaCarga,
                       EstadoImportacion estado, Long usuarioId) {
        this.nombreArchivo = nombreArchivo;
        this.fechaCarga = fechaCarga;
        this.estado = estado;
        this.usuarioId = usuarioId;
    }

    public void adjuntarArchivo(byte[] contenido, String contentType) {
        this.contenidoArchivo = contenido;
        this.contentType = contentType;
        this.tamanoBytes = contenido == null ? 0L : (long) contenido.length;
        this.hashSha256 = contenido == null ? null : sha256(contenido);
    }

    public void registrarConfirmacion(int notasEnviadas, int notasRegistradas) {
        this.fechaConfirmacion = LocalDateTime.now();
        this.notasEnviadas = notasEnviadas;
        this.notasRegistradas = notasRegistradas;
    }

    private static String sha256(byte[] contenido) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public Long getId() { return id; }

    public String getNombreArchivo() { return nombreArchivo; }

    public LocalDateTime getFechaCarga() { return fechaCarga; }

    public EstadoImportacion getEstado() { return estado; }
    public void setEstado(EstadoImportacion estado) { this.estado = estado; }

    public Integer getTotalRegistros() { return totalRegistros; }
    public void setTotalRegistros(Integer totalRegistros) { this.totalRegistros = totalRegistros; }

    public Integer getRegistrosValidos() { return registrosValidos; }
    public void setRegistrosValidos(Integer registrosValidos) { this.registrosValidos = registrosValidos; }

    public Integer getRegistrosInvalidos() { return registrosInvalidos; }
    public void setRegistrosInvalidos(Integer registrosInvalidos) { this.registrosInvalidos = registrosInvalidos; }

    public Long getUsuarioId() { return usuarioId; }

    public byte[] getContenidoArchivo() { return contenidoArchivo; }
    public Long getTamanoBytes() { return tamanoBytes; }
    public String getContentType() { return contentType; }
    public String getHashSha256() { return hashSha256; }
    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public Integer getNotasEnviadas() { return notasEnviadas; }
    public Integer getNotasRegistradas() { return notasRegistradas; }
}