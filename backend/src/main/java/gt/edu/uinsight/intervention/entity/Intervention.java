// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA de la tabla {@code intervention}: una intervención institucional
 * ejecutada como respuesta a una alerta académica (RN-1, RN-2, RN-3, RN-4).
 */
@Entity
@Table(name = "intervention")
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Decisión de diseño: NO se modela como @ManyToOne hacia una entidad Alert.
     * La tabla `alert` pertenece a la célula C3 y su entidad JPA todavía no existe
     * en el monorepo. Se guarda el id crudo en una columna simple para no acoplar
     * este módulo a código que no controlamos; la existencia y el estado de la
     * alerta se validan vía AlertValidationPort (ver intervention.service),
     * actualmente resuelto con un adapter JDBC temporal (BKL-07).
     */
    @Column(name = "alert_id", nullable = false)
    private Long alertId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private InterventionType type;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "responsible", nullable = false, length = 150)
    private String responsible;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InterventionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Intervention() {
        // Requerido por JPA
    }

    public Intervention(Long alertId, InterventionType type, String description,
                         String responsible, LocalDate startDate) {
        this.alertId = alertId;
        this.type = type;
        this.description = description;
        this.responsible = responsible;
        this.startDate = startDate;
        this.status = InterventionStatus.PLANNED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = InterventionStatus.PLANNED;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getAlertId() {
        return alertId;
    }

    public InterventionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getResponsible() {
        return responsible;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public InterventionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
