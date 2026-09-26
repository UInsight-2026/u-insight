package gt.edu.uinsight.imports.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Detalle por fila de una Importacion. Entidad diseñada por Adolfo Gonzales
 * (célula A7, sección 5 de la cédula).
 *
 * El parseo del CSV que llena estos registros y las 6 reglas de negocio que
 * determinan estadoValidacion/campoError/motivoError se implementan en la
 * Semana 3 (tareas 17 y 18 del backlog); en la Semana 2 solo se define la
 * estructura de la entidad y su repositorio (tarea 12).
 */
@Entity
@Table(name = "registro_importacion")
public class RegistroImportacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "importacion_id", nullable = false)
    private Long importacionId;

    @Column(name = "numero_fila", nullable = false)
    private Integer numeroFila;

    @Column(name = "student_code", nullable = false, length = 50)
    private String studentCode;

    @Column(name = "evaluation_code", nullable = false, length = 50)
    private String evaluationCode;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_validacion", nullable = false, length = 20)
    private EstadoValidacion estadoValidacion;

    @Column(name = "campo_error", length = 50)
    private String campoError;

    @Column(name = "motivo_error", length = 255)
    private String motivoError;

    protected RegistroImportacion() {
        // constructor requerido por JPA
    }

    public RegistroImportacion(Long importacionId, Integer numeroFila,
                                String studentCode, String evaluationCode,
                                BigDecimal score, EstadoValidacion estadoValidacion) {
        this.importacionId = importacionId;
        this.numeroFila = numeroFila;
        this.studentCode = studentCode;
        this.evaluationCode = evaluationCode;
        this.score = score;
        this.estadoValidacion = estadoValidacion;
    }

    public Long getId() {
        return id;
    }

    public Long getImportacionId() {
        return importacionId;
    }

    public void setImportacionId(Long importacionId) {
        this.importacionId = importacionId;
    }

    public Integer getNumeroFila() {
        return numeroFila;
    }

    public void setNumeroFila(Integer numeroFila) {
        this.numeroFila = numeroFila;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getEvaluationCode() {
        return evaluationCode;
    }

    public void setEvaluationCode(String evaluationCode) {
        this.evaluationCode = evaluationCode;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public EstadoValidacion getEstadoValidacion() {
        return estadoValidacion;
    }

    public void setEstadoValidacion(EstadoValidacion estadoValidacion) {
        this.estadoValidacion = estadoValidacion;
    }

    public String getCampoError() {
        return campoError;
    }

    public void setCampoError(String campoError) {
        this.campoError = campoError;
    }

    public String getMotivoError() {
        return motivoError;
    }

    public void setMotivoError(String motivoError) {
        this.motivoError = motivoError;
    }
}
