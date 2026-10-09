
package gt.edu.uinsight.analytics.dispersion.service;

import gt.edu.uinsight.analytics.dispersion.calculator.DispersionCalculator;
import gt.edu.uinsight.analytics.dispersion.classifier.DispersionClassifier;
import gt.edu.uinsight.analytics.dispersion.dto.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.exception.CursoNoEncontradoException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInsuficientesException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;
import gt.edu.uinsight.analytics.dispersion.exception.SeccionNoEncontradaException;
import gt.edu.uinsight.analytics.dispersion.mapper.DispersionMapper;
import gt.edu.uinsight.analytics.dispersion.repository.DispersionGradeRepository;
import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;
import gt.edu.uinsight.academic.section.repository.SectionRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public DispersionResponse calcularPorSeccion(Long sectionId) {
        validarId(sectionId, "sección");

        if (!sectionRepository.existsById(sectionId)) {
            throw new SeccionNoEncontradaException(sectionId);
        }

        List<BigDecimal> scores =
                dispersionGradeRepository.findScoresBySectionId(sectionId);

        validarDatos(scores);

        BigDecimal min = dispersionCalculator.calcularMinimo(scores);
        BigDecimal max = dispersionCalculator.calcularMaximo(scores);
        BigDecimal range = dispersionCalculator.calcularRango(scores);
        BigDecimal variance = dispersionCalculator.calcularVarianza(scores);
        BigDecimal standardDeviation =
                dispersionCalculator.calcularDesviacionEstandar(scores);

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
    public DispersionResponse calcularPorCurso(Long courseId) {
        validarId(courseId, "curso");

        if (sectionRepository.findByCourseId(courseId).isEmpty()) {
            throw new CursoNoEncontradoException(courseId);
        }

        List<BigDecimal> scores =
                dispersionGradeRepository.findScoresByCourseId(courseId);

        validarDatos(scores);

        BigDecimal min = dispersionCalculator.calcularMinimo(scores);
        BigDecimal max = dispersionCalculator.calcularMaximo(scores);
        BigDecimal range = dispersionCalculator.calcularRango(scores);
        BigDecimal variance = dispersionCalculator.calcularVarianza(scores);
        BigDecimal standardDeviation =
                dispersionCalculator.calcularDesviacionEstandar(scores);

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
                    "El ID del " + tipo + " debe ser válido.");
        }
    }

    private void validarDatos(List<BigDecimal> scores) {
        DispersionValidator.validar(scores);
    }
}
