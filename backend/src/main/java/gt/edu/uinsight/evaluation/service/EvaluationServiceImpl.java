package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.request.UpdateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.entity.Evaluation;
import gt.edu.uinsight.evaluation.mapper.EvaluationMapper;
import gt.edu.uinsight.evaluation.repository.EvaluationRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    // Estados válidos del sistema (mientras no se use un Enum en la Entity)
    private static final Set<String> VALID_STATUSES = Set.of("DRAFT", "ACTIVE", "CLOSED", "CANCELLED");

    // RN6: transiciones válidas. DRAFT -> ACTIVE -> CLOSED, o DRAFT/ACTIVE -> CANCELLED.
    // No se permiten transiciones inversas ni salir de CLOSED/CANCELLED (RN5 también aplica aquí).
    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            "DRAFT", Set.of("ACTIVE", "CANCELLED"),
            "ACTIVE", Set.of("CLOSED", "CANCELLED"),
            "CLOSED", Set.of(),
            "CANCELLED", Set.of()
    );

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
        Evaluation entity = findEvaluationOrThrow(id);
        return evaluationMapper.toResponse(entity);
    }

    @Override
    public List<EvaluationResponse> getEvaluationsBySectionId(Long sectionId) {
        // HU2 — GET /api/v1/sections/{id}/evaluations
        // No se valida aquí que la sección exista (eso depende de la célula A4);
        // si no hay evaluaciones, el contrato espera 200 + lista vacía, no 404.
        return evaluationRepository.findBySectionId(sectionId).stream()
                .map(evaluationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EvaluationResponse updateEvaluation(Long id, UpdateEvaluationRequest request) {
        Evaluation entity = findEvaluationOrThrow(id);

        // RN5 — una evaluación CLOSED no debe modificarse
        if ("CLOSED".equals(entity.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede modificar una evaluación en estado CLOSED");
        }

        entity.setName(request.getName());
        entity.setEvaluationDate(request.getEvaluationDate());
        entity.setMaximumScore(request.getMaximumScore());
        entity.setWeight(request.getWeight());

        Evaluation updated = evaluationRepository.save(entity);
        return evaluationMapper.toResponse(updated);
    }

    @Override
    public EvaluationResponse changeStatus(Long id, String newStatus) {
        Evaluation entity = findEvaluationOrThrow(id);

        if (!VALID_STATUSES.contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado inválido: " + newStatus + ". Valores permitidos: " + VALID_STATUSES);
        }

        String currentStatus = entity.getStatus();
        Set<String> allowedNextStates = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());

        // RN6 — transición inválida (incluye intentar salir de CLOSED/CANCELLED, cubriendo también RN5)
        if (!allowedNextStates.contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transición de estado no permitida: " + currentStatus + " -> " + newStatus);
        }

        entity.setStatus(newStatus);
        Evaluation updated = evaluationRepository.save(entity);
        return evaluationMapper.toResponse(updated);
    }

    private Evaluation findEvaluationOrThrow(Long id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró la evaluación con el ID: " + id));
    }
}
