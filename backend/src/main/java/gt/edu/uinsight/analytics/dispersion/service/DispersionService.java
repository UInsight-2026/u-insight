
package gt.edu.uinsight.analytics.dispersion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.dispersion.dto.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.repository.DispersionGradeRepository;
import gt.edu.uinsight.analytics.dispersion.exception.DatosInsuficientesException;
import gt.edu.uinsight.analytics.dispersion.exception.DatosInvalidosException;

@Service
public class DispersionService {

    private final DispersionGradeRepository gradeRepository;
    private final DispersionCalculator calculator;

    public DispersionService(
            DispersionGradeRepository gradeRepository,
            DispersionCalculator calculator) {

        this.gradeRepository = gradeRepository;
        this.calculator = calculator;
    }

    public DispersionResponse getSectionDispersion(Long sectionId) {

        if (sectionId == null || sectionId <= 0) {
            throw new DatosInvalidosException(
                    "El ID de la sección debe ser válido"
            );
        }

        List<Double> grades =
                gradeRepository.findGradesBySectionId(sectionId);

        if (grades == null || grades.size() < 2) {
            throw new DatosInsuficientesException(
                    "La sección necesita al menos dos calificaciones para calcular la dispersión"
            );
        }

        for (Double grade : grades) {

            if (grade == null || grade < 0 || grade > 100) {
                throw new DatosInvalidosException(
                        "Se encontraron calificaciones inválidas"
                );
            }
        }

        return calculator.calculate(sectionId, grades);
    }
}
