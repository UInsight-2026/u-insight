package gt.edu.uinsight.analytics.position.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MockGradeIntegrationService implements GradeIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(MockGradeIntegrationService.class);
    
    // Estos son los datos que tenías antes en tu PositionService
    private static final List<Double> MOCK_GRADES = 
            Arrays.asList(60.0, 72.0, 85.0, 90.0, 55.0, 78.0, 88.0, 92.0, 67.0, 74.0);

    @Override
    public List<Double> getGradesBySection(Long sectionId) {
        log.info("Simulando llamada a Celula A6 para obtener notas de la seccion {}", sectionId);
        return MOCK_GRADES;
    }

    @Override
    public List<Double> getGradesByStudent(Long studentId) {
        log.info("Simulando llamada a Celula A6 para obtener notas del estudiante {}", studentId);
        return MOCK_GRADES;
    }
}