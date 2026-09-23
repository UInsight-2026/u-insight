package gt.edu.uinsight.analytics.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gt.edu.uinsight.analytics.trend.entity.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
    
}
