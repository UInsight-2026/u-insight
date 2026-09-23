package gt.edu.uinsight.analytics.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gt.edu.uinsight.analytics.trend.entity.Trend;

public interface TrendRepository extends JpaRepository<Trend, Long> {
    
}
