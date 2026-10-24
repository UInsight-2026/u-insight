package gt.edu.uinsight.alert.repository;

import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.model.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByStatusNotIn(Collection<AlertStatus> statuses);

    boolean existsBySectionIdAndAlertTypeAndTitleAndStatusNotIn(
            Integer sectionId,
            String alertType,
            String title,
            Collection<AlertStatus> statuses);
}
