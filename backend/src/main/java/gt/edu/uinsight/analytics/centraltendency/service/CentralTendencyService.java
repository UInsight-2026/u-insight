package gt.edu.uinsight.analytics.centraltendency.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

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

        return calculate("section_central_tendency", sectionId,
                () -> gradeDataRepository.findBySectionId(sectionId, evaluationId));
    }

    public CentralTendencyResponse getCourseCentralTendency(Long courseId, Long periodId) {
        requirePositive(courseId, "courseId");
        requirePositiveIfPresent(periodId, "periodId");

        return calculate("course_central_tendency", courseId,
                () -> gradeDataRepository.findByCourseId(courseId, periodId));
    }

    private CentralTendencyResponse calculate(
            String operation,
            Long resourceId,
            Supplier<List<GradeData>> gradeLoader) {
        log.info("service=B1 event=operation_started operation={} resourceId={}", operation, resourceId);

        CentralTendencyResponse response = buildResponse(gradeLoader.get());

        log.info("service=B1 event=operation_succeeded operation={} resourceId={} sampleSize={}",
                operation, resourceId, response.sampleSize());
        return response;
    }

    private CentralTendencyResponse buildResponse(List<GradeData> grades) {
        if (grades == null) {
            return CentralTendencyResponse.empty();
        }
        List<BigDecimal> scores = grades.stream().map(grade -> grade.getScore()).toList();
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
