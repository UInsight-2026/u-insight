package gt.edu.uinsight.system.repository;

import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.entity.SystemCheckLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemCheckLogRepository extends JpaRepository<SystemCheckLog, Long> {

    /**
     * Listado con filtros opcionales y paginacion. Un filtro nulo no restringe:
     * asi los dos parametros son combinables sin necesidad de consultas separadas.
     */
    @Query("""
            select log from SystemCheckLog log
            where (:component is null or lower(log.component) like lower(concat('%', :component, '%')))
              and (:status is null or log.status = :status)
            """)
    Page<SystemCheckLog> findByFilters(
            @Param("component") String component,
            @Param("status") CheckStatus status,
            Pageable pageable);
}
