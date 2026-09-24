package gt.edu.uinsight.indicatorconfiguration.repository;

import gt.edu.uinsight.indicatorconfiguration.entity.IndicatorConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndicatorConfigurationRepository extends JpaRepository<IndicatorConfiguration, Long> {

    Optional<IndicatorConfiguration> findByKey(String key);

    boolean existsByKey(String key);
}
