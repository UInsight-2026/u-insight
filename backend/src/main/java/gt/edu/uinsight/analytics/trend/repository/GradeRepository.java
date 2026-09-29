package gt.edu.uinsight.analytics.trend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.trend.entity.Grade;

// Parche de arranque aportado por C7: la celula B3 (dispersion) declara otra interfaz
// llamada GradeRepository. Spring deriva el nombre del bean del nombre simple de la
// interfaz, asi que ambas peleaban por 'gradeRepository' y el contexto no arrancaba
// (BeanDefinitionOverrideException). Se nombra explicitamente este bean; la inyeccion
// por tipo no cambia.
@Repository("trendGradeRepository")
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Parche de compilacion aportado por C7. TrendService:79 (celula B4, PR #43) invoca
    // este metodo, pero nunca fue declarado y el proyecto entero no compilaba.
    // No se puede resolver como derived query: la entidad Grade guarda evaluationId como
    // Long, no tiene una relacion 'evaluation'. Se implementa con la misma consulta nativa
    // que usa el GradeRepository de dispersion, conservando el nombre original del metodo
    // para no tocar el servicio de B4.
    // Pendiente: trasladarlo a la celula B4 para que lo adopte en develop.
    @Query(value = "SELECT g.* FROM grade g INNER JOIN evaluation e ON g.evaluation_id = e.id "
            + "WHERE g.student_id = :studentId ORDER BY e.evaluation_date ASC", nativeQuery = true)
    List<Grade> findByStudentIdOrderByEvaluation_EvaluationDateAsc(@Param("studentId") Long studentId);
}
