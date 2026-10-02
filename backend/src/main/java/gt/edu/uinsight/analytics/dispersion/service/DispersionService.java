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

@Service
public class DispersionService {

    private final DispersionGradeRepository gradeRepository;
    private final DispersionCalculator calculator;
    private final DispersionMapper mapper;

    public DispersionService(
            DispersionGradeRepository gradeRepository,
            DispersionCalculator calculator,
            DispersionMapper mapper) {

        this.gradeRepository = gradeRepository;
        this.calculator = calculator;
        this.mapper = mapper;
    }

    public DispersionResponse getSectionDispersion(Long sectionId) {

        validarId(sectionId, "sección");

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
         * Aquí deben utilizarse los umbrales configurables
         * definidos para B3.
         */
        throw new UnsupportedOperationException(
                "Los umbrales de dispersión aún no están configurados."
        );
    }
}
