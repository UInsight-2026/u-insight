package gt.edu.uinsight.alert.repository;

import gt.edu.uinsight.alert.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;


public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    @Query("SELECT a FROM Alert a WHERE a.status NOT IN ('RESOLVED', 'DISMISSED')")
    List<Alert> findActiveAlerts();
}