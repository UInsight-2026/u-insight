package gt.edu.uinsight.academicperiod.repository;

import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import gt.edu.uinsight.academicperiod.entity.PeriodStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {

    Page<AcademicPeriod> findByStatus(PeriodStatus status, Pageable pageable);

    Optional<AcademicPeriod> findFirstByStatus(PeriodStatus status);

    boolean existsByStatus(PeriodStatus status);

    boolean existsByNameIgnoreCaseAndYear(String name, Integer year);

    boolean existsByNameIgnoreCaseAndYearAndIdNot(String name, Integer year, Long id);
}
