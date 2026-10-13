// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.entity;

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

/**
 * Entidad JPA de la tabla {@code follow_up}: un seguimiento registrado sobre
 * una intervención (RN-5, RN-6, RN-7).
 */
@Entity
@Table(name = "follow_up")
public class FollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intervention_id", nullable = false)
    private Long interventionId;

    @Column(name = "follow_up_date", nullable = false)
    private LocalDate followUpDate;

    @Column(name = "observation", nullable = false, length = 500)
    private String observation;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", length = 20)
    private FollowUpResult result;

    /** RN-7: el borrado es siempre lógico, nunca físico. */
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    protected FollowUp() {
        // Requerido por JPA
    }

    public FollowUp(Long interventionId, LocalDate followUpDate, String observation, FollowUpResult result) {
        this.interventionId = interventionId;
        this.followUpDate = followUpDate;
        this.observation = observation;
        this.result = result;
    }

    @PrePersist
    protected void onCreate() {
        this.deleted = false;
    }

    public Long getId() {
        return id;
    }

    public Long getInterventionId() {
        return interventionId;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public String getObservation() {
        return observation;
    }

    public FollowUpResult getResult() {
        return result;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void markDeleted() {
        this.deleted = true;
    }
}
