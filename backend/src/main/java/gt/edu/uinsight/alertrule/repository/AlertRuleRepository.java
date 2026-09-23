package gt.edu.uinsight.alertrule.repository;

import gt.edu.uinsight.alertrule.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByActive(Boolean active);
}
