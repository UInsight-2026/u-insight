package gt.edu.uinsight.system.repository;

import gt.edu.uinsight.system.entity.SystemCheckLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemCheckLogRepository extends JpaRepository<SystemCheckLog, Long> {
}