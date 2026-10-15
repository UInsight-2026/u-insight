package gt.edu.uinsight.student.repository;

import gt.edu.uinsight.student.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {
}
