package gt.edu.uinsight.imports.repository;

import gt.edu.uinsight.imports.entity.EstudianteValido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteValidoRepository extends JpaRepository<EstudianteValido, Long> {

    boolean existsByStudentCode(String studentCode);
}