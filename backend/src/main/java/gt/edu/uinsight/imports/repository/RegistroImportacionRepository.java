package gt.edu.uinsight.imports.repository;

import gt.edu.uinsight.imports.entity.EstadoValidacion;
import gt.edu.uinsight.imports.entity.RegistroImportacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroImportacionRepository
        extends JpaRepository<RegistroImportacion, Long> {

    Page<RegistroImportacion> findByImportacionIdAndEstadoValidacion(
            Long importacionId, EstadoValidacion estadoValidacion, Pageable pageable);
}
