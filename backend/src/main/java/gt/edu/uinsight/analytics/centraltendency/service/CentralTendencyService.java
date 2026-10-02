package gt.edu.uinsight.analytics.centraltendency.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.centraltendency.calculator.CentralTendencyCalculator;
import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.centraltendency.model.GradeData;
import gt.edu.uinsight.analytics.centraltendency.repository.GradeDataRepository;

/**
 * Orquesta el calculo de tendencia central: obtiene las calificaciones del
 * repositorio y delega el calculo de media, mediana y moda a
 * CentralTendencyCalculator (B1, Javier). Este servicio no realiza calculos
 * estadisticos por si mismo.
 */
@Service
public class CentralTendencyService {

    private final GradeDataRepository gradeDataRepository;
    private final CentralTendencyCalculator centralTendencyCalculator;

    public CentralTendencyService(GradeDataRepository gradeDataRepository,
                                   CentralTendencyCalculator centralTendencyCalculator) {
        this.gradeDataRepository = gradeDataRepository;
        this.centralTendencyCalculator = centralTendencyCalculator;
    }

    public CentralTendencyResponse getSectionCentralTendency(Long sectionId, Long evaluationId) {
        List<GradeData> grades = gradeDataRepository.findBySectionId(sectionId, evaluationId);
        return buildResponse(grades);
    }

    public CentralTendencyResponse getCourseCentralTendency(Long courseId, Long periodId) {
        List<GradeData> grades = gradeDataRepository.findByCourseId(courseId, periodId);
        return buildResponse(grades);
    }

    private CentralTendencyResponse buildResponse(List<GradeData> grades) {
        List<BigDecimal> scores = grades.stream().map(GradeData::getScore).toList();
        return centralTendencyCalculator.calculate(scores);
    }
}
