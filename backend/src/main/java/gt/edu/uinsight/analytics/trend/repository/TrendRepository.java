package gt.edu.uinsight.analytics.trend.repository;

public interface TrendRepository extends JpaRepository<Trend, Long> {
    Trend findBySectionId(Long sectionId);
    Trend findByStudentId(Long studentId);
    
}
