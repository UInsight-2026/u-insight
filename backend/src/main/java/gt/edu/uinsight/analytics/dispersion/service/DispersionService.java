
package gt.edu.uinsight.analytics.dispersion.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.dispersion.calculator.DispersionCalculator;
import gt.edu.uinsight.analytics.dispersion.config.DispersionClassifier;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionRecursoNoEncontradoException;
import gt.edu.uinsight.analytics.dispersion.mapper.DispersionMapper;
import gt.edu.uinsight.analytics.dispersion.repository.DispersionGradeRepository;
import gt.edu.uinsight.analytics.dispersion.validation.DispersionValidator;
import gt.edu.uinsight.section.repository.SectionRepository;

@Service
public class DispersionService {

    private final DispersionGradeRepository gradeRepository;
    private final DispersionCalculator calculator;
    private final DispersionMapper mapper;
    private final SectionRepository sectionRepository;
    private final DispersionClassifier classifier;

    public DispersionService(
            DispersionGradeRepository gradeRepository,
            DispersionCalculator calculator,
            DispersionMapper mapper,
            SectionRepository sectionRepository,
            DispersionClassifier classifier) {

        this.gradeRepository = gradeRepository;
        this.calculator = calculator;
        this.mapper = mapper;
        this.sectionRepository = sectionRepository;
        this.classifier = classifier;
    }

    public DispersionResponse getSectionDispersion(Long sectionId) {

        validarId(sectionId, "sección");

        if (!sectionRepository.existsById(sectionId)) {
            throw new DispersionRecursoNoEncontradoException(
                    "La sección con ID " + sectionId + " no existe."
            );
        }

        List<BigDecimal> scores =
                gradeRepository.findScoresBySectionId(sectionId);

        return calcularDispersionSeccion(sectionId, scores);
    }

    public DispersionResponse getCourseDispersion(Long courseId) {

        validarId(courseId, "curso");

        if (sectionRepository.findByCourseId(courseId).isEmpty()) {
            throw new DispersionRecursoNoEncontradoException(
                    "No existe un curso con ID " + courseId
                            + " asociado a una sección."
            );
        }

        List<BigDecimal> scores =
                gradeRepository.findScoresByCourseId(courseId);

        DispersionValidator.validar(scores);

        BigDecimal min = calculator.calculateMin(scores);
        BigDecimal max = calculator.calculateMax(scores);
        BigDecimal range = calculator.calculateRange(scores);
        BigDecimal variance = calculator.calculateVariance(scores);
        BigDecimal standardDeviation =
                calculator.calculateStandardDeviation(scores);

        DispersionClassification classification =
                classifier.clasificar(standardDeviation);

        return mapper.toCourseResponse(
                courseId,
                min,
                max,
                range,
                variance,
                standardDeviation,
                classification
        );
    }

    private DispersionResponse calcularDispersionSeccion(
            Long sectionId,
            List<BigDecimal> scores) {

        DispersionValidator.validar(scores);

        BigDecimal min = calculator.calculateMin(scores);
        BigDecimal max = calculator.calculateMax(scores);
        BigDecimal range = calculator.calculateRange(scores);
        BigDecimal variance = calculator.calculateVariance(scores);
        BigDecimal standardDeviation =
                calculator.calculateStandardDeviation(scores);

        DispersionClassification classification =
                classifier.clasificar(standardDeviation);

        return mapper.toSectionResponse(
                sectionId,
                min,
                max,
                range,
                variance,
                standardDeviation,
                classification
        );
    }

    private void validarId(Long id, String tipo) {
        if (id == null || id <= 0) {
            throw new DispersionDatosInvalidosException(
                    "El ID del " + tipo + " debe ser válido."
            );
        }
    }
}
