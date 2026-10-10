package gt.edu.uinsight.alert.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import gt.edu.uinsight.alert.model.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
