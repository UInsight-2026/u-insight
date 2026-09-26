// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.repository;

import gt.edu.uinsight.followup.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {

    /**
     * RN-7: excluye los seguimientos con borrado lógico activo.
     */
    List<FollowUp> findByInterventionIdAndDeletedFalseOrderByFollowUpDateAsc(Long interventionId);
}
