
package gt.edu.uinsight.analytics.dispersion.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.edu.uinsight.analytics.dispersion.calculator.DispersionCalculator;
import gt.edu.uinsight.analytics.dispersion.config.DispersionClassifier;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.exception.CursoNoEncontradoException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;
import gt.edu.uinsight.analytics.dispersion.exception.SeccionNoEncontradaException;
import gt.edu.uinsight.analytics.dispersion.mapper.DispersionMapper;
import gt.edu.uinsight.analytics.dispersion.repository.DispersionGradeRepository;
import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;
import gt.edu.uinsight.section.service.SectionService;

@Service
public class DispersionService {

    private final DispersionGradeRepository dispersionGradeRepository;
    private final DispersionCalculator dispersionCalculator;
    private final DispersionClassifier dispersionClassifier;
    private final DispersionMapper dispersionMapper;
    private final SectionService sectionService;

    public DispersionService(
            DispersionGradeRepository dispersionGradeRepository,
            DispersionCalculator dispersionCalculator,
            DispersionClassifier dispersionClassifier,
            DispersionMapper dispersionMapper,
            SectionService sectionService) {

        this.dispersionGradeRepository = dispersionGradeRepository;
        this.dispersionCalculator = dispersionCalculator;
        this.dispersionClassifier = dispersionClassifier;
        this.dispersionMapper = dispersionMapper;
        this.sectionService = sectionService;
    }

    @Transactional(readOnly = true)
    public DispersionResponse getSectionDispersion(Long sectionId) {
        validarId(sectionId, "sección");

        if (!sectionService.existsById(sectionId)) {
            throw new SeccionNoEncontradaException(sectionId);
        }
        Long courseId = sectionService.getSectionById(sectionId).getCourseId();

        List<BigDecimal> scores =
                dispersionGradeRepository.findScoresBySectionId(sectionId);

        DispersionMetrics metrics = calculateMetrics(scores);

        return dispersionMapper.toSectionResponse(
                sectionId,
                courseId,
                metrics.min(),
                metrics.max(),
                metrics.range(),
                metrics.variance(),
                metrics.standardDeviation(),
                metrics.classification());
    }

    @Transactional(readOnly = true)
    public DispersionResponse getCourseDispersion(Long courseId) {
        validarId(courseId, "curso");

        /*
         * Comprobación provisional:
         * verifica que el curso tenga al menos una sección.
         * Para distinguir entre un curso inexistente y uno sin
         * secciones, se necesita consultar el repositorio de Course.
         */
        if (!dispersionGradeRepository.courseExists(courseId)) {
            throw new CursoNoEncontradoException(courseId);
        }

        List<BigDecimal> scores =
                dispersionGradeRepository.findScoresByCourseId(courseId);

        DispersionMetrics metrics = calculateMetrics(scores);

        return dispersionMapper.toCourseResponse(
                courseId,
                metrics.min(),
                metrics.max(),
                metrics.range(),
                metrics.variance(),
                metrics.standardDeviation(),
                metrics.classification());
    }

    private DispersionMetrics calculateMetrics(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);

        BigDecimal min = dispersionCalculator.calculateMin(scores);
        BigDecimal max = dispersionCalculator.calculateMax(scores);
        BigDecimal range = dispersionCalculator.calculateRange(scores);
        BigDecimal variance = dispersionCalculator.calculateVariance(scores);
        BigDecimal standardDeviation = dispersionCalculator.calculateStandardDeviation(scores);

        return new DispersionMetrics(
                min,
                max,
                range,
                variance,
                standardDeviation,
                dispersionClassifier.clasificar(standardDeviation));
    }

    private void validarId(Long id, String tipo) {
        if (id == null || id <= 0) {
            throw new DispersionDatosInvalidosException(
                    "El ID de " + tipo + " debe ser un número positivo.");
        }
    }

    private record DispersionMetrics(
            BigDecimal min,
            BigDecimal max,
            BigDecimal range,
            BigDecimal variance,
            BigDecimal standardDeviation,
            DispersionClassification classification) {
    }
}
