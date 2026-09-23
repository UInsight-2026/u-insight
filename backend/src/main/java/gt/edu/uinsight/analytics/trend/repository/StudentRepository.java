package gt.edu.uinsight.analytics.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gt.edu.uinsight.analytics.trend.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Aquí puedes definir métodos de consulta personalizados si es necesario
    
}
