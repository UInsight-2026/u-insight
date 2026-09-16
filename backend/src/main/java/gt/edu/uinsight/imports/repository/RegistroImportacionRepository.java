package gt.edu.uinsight.imports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.imports.entity.RegistroImportacion;


@Repository
public interface RegistroImportacionRepository
        extends JpaRepository<RegistroImportacion, Long> {
}
