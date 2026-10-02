package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.request.UpdateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.entity.Evaluation;
import gt.edu.uinsight.evaluation.mapper.EvaluationMapper;
import gt.edu.uinsight.evaluation.repository.EvaluationRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository, EvaluationMapper evaluationMapper) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
    }

    @Override
    public EvaluationResponse createEvaluation(CreateEvaluationRequest request) {
        Evaluation entity = evaluationMapper.toEntity(request);
        Evaluation savedEntity = evaluationRepository.save(entity);
        return evaluationMapper.toResponse(savedEntity);
    }

    @Override
    public List<EvaluationResponse> getAllEvaluations() {
        return evaluationRepository.findAll().stream()
                .map(evaluationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EvaluationResponse getEvaluationById(Long id) {
        // Usamos la excepción nativa de Spring para no depender de otras células de momento
        Evaluation entity = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la evaluación con el ID: " + id));
        
        return evaluationMapper.toResponse(entity);
    }

    @Override
    public List<EvaluationResponse> getEvaluationsBySectionId(Long sectionId) {
        // TODO: Implementar lógica de la HU2 (Buscar en el repository por sección)
        return null;
    }

    @Override
    public EvaluationResponse updateEvaluation(Long id, UpdateEvaluationRequest request) {
        // TODO: Implementar lógica de actualización
        return null;
    }
}
