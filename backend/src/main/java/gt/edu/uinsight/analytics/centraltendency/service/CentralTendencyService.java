package gt.edu.uinsight.analytics.centraltendency.service;

import java.math.BigDecimal;
import java.util.List;

// Importaciones manuales para el Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.centraltendency.calculator.CentralTendencyCalculator;
import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.centraltendency.exception.InvalidAnalyticsRequestException;
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

    // Declaración del Logger manual (sin usar @Slf4j)
    private static final Logger log = LoggerFactory.getLogger(CentralTendencyService.class);

    private final GradeDataRepository gradeDataRepository;
    private final CentralTendencyCalculator centralTendencyCalculator;

    public CentralTendencyService(GradeDataRepository gradeDataRepository,
                                   CentralTendencyCalculator centralTendencyCalculator) {
        this.gradeDataRepository = gradeDataRepository;
        this.centralTendencyCalculator = centralTendencyCalculator;
    }

    public CentralTendencyResponse getSectionCentralTendency(Long sectionId, Long evaluationId) {
        requirePositive(sectionId, "sectionId");
        requirePositiveIfPresent(evaluationId, "evaluationId");

        // Log de inicio exactamente como lo pide el documento
        log.info("service=B1 event=operation_started operation=section_central_tendency resourceId={}", sectionId);

        List<GradeData> grades = gradeDataRepository.findBySectionId(sectionId, evaluationId);
        CentralTendencyResponse response = buildResponse(grades);

        // Log de éxito exactamente como lo pide el documento
        log.info("service=B1 event=operation_succeeded operation=section_central_tendency resourceId={} sampleSize={}",
                 sectionId, response.sampleSize());

        return response;
    }

    public CentralTendencyResponse getCourseCentralTendency(Long courseId, Long periodId) {
        requirePositive(courseId, "courseId");
        requirePositiveIfPresent(periodId, "periodId");

        // Log de inicio adaptado para el cálculo de un curso completo
        log.info("service=B1 event=operation_started operation=course_central_tendency resourceId={}", courseId);

        List<GradeData> grades = gradeDataRepository.findByCourseId(courseId, periodId);
        CentralTendencyResponse response = buildResponse(grades);

        // Log de éxito adaptado para el cálculo de un curso completo
        log.info("service=B1 event=operation_succeeded operation=course_central_tendency resourceId={} sampleSize={}",
                 courseId, response.sampleSize());

        return response;
    }

    private CentralTendencyResponse buildResponse(List<GradeData> grades) {
        if (grades == null) {
            return CentralTendencyResponse.empty();
        }
        List<BigDecimal> scores = grades.stream().map(GradeData::getScore).toList();
        return centralTendencyCalculator.calculate(scores);
    }

    private static void requirePositive(Long value, String fieldName) {
        if (value == null || value <= 0) {
            throw new InvalidAnalyticsRequestException(fieldName + " debe ser mayor que cero");
        }
    }

    private static void requirePositiveIfPresent(Long value, String fieldName) {
        if (value != null && value <= 0) {
            throw new InvalidAnalyticsRequestException(fieldName + " debe ser mayor que cero");
        }
    }
}
