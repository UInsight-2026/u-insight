package gt.edu.uinsight.intervention.repository;

import gt.edu.uinsight.intervention.entity.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    List<Intervention> findByAlertIdOrderByStartDateAsc(Long alertId);
}
