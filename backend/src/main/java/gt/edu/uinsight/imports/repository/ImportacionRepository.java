package gt.edu.uinsight.imports.repository;

import gt.edu.uinsight.imports.entity.Importacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA de Importacion (tarea 12 del backlog).
 */
@Repository
public interface ImportacionRepository extends JpaRepository<Importacion, Long> {
    
}
