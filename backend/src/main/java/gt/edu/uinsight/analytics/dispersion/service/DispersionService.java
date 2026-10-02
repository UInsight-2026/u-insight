package gt.edu.uinsight.analytics.dispersion.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.dispersion.calculator.DispersionCalculator;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;
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

    public DispersionService(
            DispersionGradeRepository gradeRepository,
            DispersionCalculator calculator,
            DispersionMapper mapper,
            SectionRepository sectionRepository) {

        this.gradeRepository = gradeRepository;
        this.calculator = calculator;
        this.mapper = mapper;
        this.sectionRepository = sectionRepository;
    }

    public DispersionResponse getSectionDispersion(Long sectionId) {

        validarId(sectionId, "sección");

        if (!sectionRepository.existsById(sectionId)) {
            throw new DispersionDatosInvalidosException(
                    "La sección con ID " + sectionId + " no existe."
            );
        }

        List<BigDecimal> scores =
                gradeRepository.findScoresBySectionId(sectionId);

        DispersionValidator.validar(scores);

        BigDecimal min =
                calculator.calculateMin(scores);

        BigDecimal max =
                calculator.calculateMax(scores);

        BigDecimal range =
                calculator.calculateRange(scores);

        BigDecimal variance =
                calculator.calculateVariance(scores);

        BigDecimal standardDeviation =
                calculator.calculateStandardDeviation(scores);

        DispersionClassification classification =
                classify(standardDeviation);

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

    public DispersionResponse getCourseDispersion(Long courseId) {

        validarId(courseId, "curso");

        if (sectionRepository.findByCourseId(courseId).isEmpty()) {
            throw new DispersionDatosInvalidosException(
                    "No existe un curso con ID " + courseId
                            + " asociado a una sección."
            );
        }

        List<BigDecimal> scores =
                gradeRepository.findScoresByCourseId(courseId);

        DispersionValidator.validar(scores);

        BigDecimal min =
                calculator.calculateMin(scores);

        BigDecimal max =
                calculator.calculateMax(scores);

        BigDecimal range =
                calculator.calculateRange(scores);

        BigDecimal variance =
                calculator.calculateVariance(scores);

        BigDecimal standardDeviation =
                calculator.calculateStandardDeviation(scores);

        DispersionClassification classification =
                classify(standardDeviation);

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

    private void validarId(Long id, String tipo) {

        if (id == null || id <= 0) {
            throw new DispersionDatosInvalidosException(
                    "El ID de la " + tipo + " debe ser válido."
            );
        }
    }

    private DispersionClassification classify(
            BigDecimal standardDeviation) {

        /*
         * Aquí se deben utilizar los umbrales configurables
         * definidos en la especificación de B3.
         *
         * No se colocan valores arbitrarios.
         */
        throw new UnsupportedOperationException(
                "Los umbrales de dispersión aún no están configurados."
        );
    }
}
