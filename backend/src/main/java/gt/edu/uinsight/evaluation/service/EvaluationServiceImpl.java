package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.entity.Evaluation;
import gt.edu.uinsight.evaluation.mapper.EvaluationMapper;
import gt.edu.uinsight.evaluation.repository.EvaluationRepository;
import java.util.List;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

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
        // (Punto 6), aquí lanzaremos un 404.
        Evaluation entity = evaluationRepository.findById(id).orElse(null);
        return evaluationMapper.toResponse(entity);
    }
}
