package gt.edu.uinsight.analytics.trend.service;

import org.springframework.stereotype.Service;

@Service 
public class TrendService {
    private final TrendRepository trendRepository;
    private final TrendMapper trendMapper;

    public TrendService(TrendRepository trendRepository, TrendMapper trendMapper) {
        this.trendRepository = trendRepository;
        this.trendMapper = trendMapper;
    }

    public TrendResponse getTrendBySectionId(Long sectionId) {
        // Lógica para obtener la tendencia por ID de sección
        Trend trend = trendRepository.findBySectionId(sectionId);
        return trendMapper.toTrendResponse(trend);
    }

    public TrendResponse getTrendByStudentId(Long studentId) {
        // Lógica para obtener la tendencia por ID de estudiante
        Trend trend = trendRepository.findByStudentId(studentId);
        return trendMapper.toTrendResponse(trend);
    }


}
