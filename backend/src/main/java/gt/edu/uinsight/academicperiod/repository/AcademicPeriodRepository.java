package gt.edu.uinsight.academicperiod.repository;

import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {

    Optional<AcademicPeriod> findByNameIgnoreCaseAndYear(String name, Integer year);
}
