package gt.edu.uinsight.system.repository;

import gt.edu.uinsight.system.model.SystemCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemCheckRepository extends JpaRepository<SystemCheck, Long> {

    Page<SystemCheck> findByStatusContainingIgnoreCaseAndComponentContainingIgnoreCase(
            String status, String component, Pageable pageable
    );
}