
package gt.edu.uinsight.analytics.dispersion.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gt.edu.uinsight.analytics.dispersion.calculator.DispersionCalculator;
import gt.edu.uinsight.analytics.dispersion.config.DispersionClassifier;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.exception.CursoNoEncontradoException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;
import gt.edu.uinsight.analytics.dispersion.exception.SeccionNoEncontradaException;
import gt.edu.uinsight.analytics.dispersion.mapper.DispersionMapper;
import gt.edu.uinsight.analytics.dispersion.repository.DispersionGradeRepository;
import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;
import gt.edu.uinsight.section.repository.SectionRepository;

@Service
public class DispersionService {

    private final DispersionGradeRepository dispersionGradeRepository;
    private final DispersionCalculator dispersionCalculator;
    private final DispersionClassifier dispersionClassifier;
    private final DispersionMapper dispersionMapper;
    private final SectionRepository sectionRepository;

    public DispersionService(
            DispersionGradeRepository dispersionGradeRepository,
            DispersionCalculator dispersionCalculator,
            DispersionClassifier dispersionClassifier,
            DispersionMapper dispersionMapper,
            SectionRepository sectionRepository) {

        this.dispersionGradeRepository = dispersionGradeRepository;
        this.dispersionCalculator = dispersionCalculator;
        this.dispersionClassifier = dispersionClassifier;
        this.dispersionMapper = dispersionMapper;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public DispersionResponse getSectionDispersion(Long sectionId) {
        validarId(sectionId, "sección");

        if (!sectionRepository.existsById(sectionId)) {
            throw new SeccionNoEncontradaException(sectionId);
        }

        List<BigDecimal> scores =
                dispersionGradeRepository.findScoresBySectionId(sectionId);

        DispersionValidator.validar(scores);

        BigDecimal min = dispersionCalculator.calculateMin(scores);
        BigDecimal max = dispersionCalculator.calculateMax(scores);
        BigDecimal range = dispersionCalculator.calculateRange(scores);
        BigDecimal variance = dispersionCalculator.calculateVariance(scores);
        BigDecimal standardDeviation =
                dispersionCalculator.calculateStandardDeviation(scores);

        var classification =
                dispersionClassifier.clasificar(standardDeviation);

        return dispersionMapper.toSectionResponse(
                sectionId,
                min,
                max,
                range,
                variance,
                standardDeviation,
                classification);
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
        if (sectionRepository.findByCourseId(courseId).isEmpty()) {
            throw new CursoNoEncontradoException(courseId);
        }

        List<BigDecimal> scores =
                dispersionGradeRepository.findScoresByCourseId(courseId);

        DispersionValidator.validar(scores);

        BigDecimal min = dispersionCalculator.calculateMin(scores);
        BigDecimal max = dispersionCalculator.calculateMax(scores);
        BigDecimal range = dispersionCalculator.calculateRange(scores);
        BigDecimal variance = dispersionCalculator.calculateVariance(scores);
        BigDecimal standardDeviation =
                dispersionCalculator.calculateStandardDeviation(scores);

        var classification =
                dispersionClassifier.clasificar(standardDeviation);

        return dispersionMapper.toCourseResponse(
                courseId,
                min,
                max,
                range,
                variance,
                standardDeviation,
                classification);
    }

    private void validarId(Long id, String tipo) {
        if (id == null || id <= 0) {
            throw new DispersionDatosInvalidosException(
                    "El ID de " + tipo + " debe ser un número positivo.");
        }
    }
}
