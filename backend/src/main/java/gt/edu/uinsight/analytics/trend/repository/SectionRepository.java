package gt.edu.uinsight.analytics.trend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.trend.entity.Section;

@Repository("trendSectionRepository")
public interface SectionRepository extends JpaRepository<Section, Long> {
    
}
