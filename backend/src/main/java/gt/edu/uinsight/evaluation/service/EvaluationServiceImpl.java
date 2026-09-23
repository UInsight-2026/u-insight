// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.dto.request.ChangeEvaluationStatusRequest;
import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.request.UpdateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.entity.Evaluation;
import gt.edu.uinsight.evaluation.exception.EvaluationNotEditableException;
import gt.edu.uinsight.evaluation.exception.EvaluationNotFoundException;
import gt.edu.uinsight.evaluation.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.evaluation.exception.SectionNotActiveException;
import gt.edu.uinsight.evaluation.exception.SectionNotFoundException;
import gt.edu.uinsight.evaluation.exception.WeightLimitExceededException;
import gt.edu.uinsight.evaluation.mapper.EvaluationMapper;
import gt.edu.uinsight.evaluation.repository.EvaluationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orquesta la lógica de negocio del módulo de evaluaciones (RN1, RN4, RN5, RN6).
 * RN2 y RN3 (nota máxima y ponderación > 0) ya se validan con Bean Validation
 * en CreateEvaluationRequest / UpdateEvaluationRequest.
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private static final BigDecimal WEIGHT_LIMIT = new BigDecimal("100.00");
    private static final String DRAFT = "DRAFT";
    private static final String ACTIVE = "ACTIVE";
    private static final String CLOSED = "CLOSED";
    private static final String CANCELLED = "CANCELLED";

    // RN6: transiciones de estado permitidas
    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = buildTransitions();

    private static Map<String, Set<String>> buildTransitions() {
        Map<String, Set<String>> transitions = new java.util.HashMap<>();
        transitions.put(DRAFT, Set.of(ACTIVE, CANCELLED));
        transitions.put(ACTIVE, Set.of(CLOSED, CANCELLED));
        transitions.put(CLOSED, Set.of());
        transitions.put(CANCELLED, Set.of());
        return transitions;
    }

    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final SectionValidationPort sectionValidationPort;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository, EvaluationMapper evaluationMapper,
                                  SectionValidationPort sectionValidationPort) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
        this.sectionValidationPort = sectionValidationPort;
    }

    @Override
    @Transactional
    public EvaluationResponse createEvaluation(CreateEvaluationRequest request) {
        assertSectionActive(request.getSectionId()); // RN1
        assertWeightWithinLimit(request.getSectionId(), request.getWeight(), null); // RN4

        Evaluation entity = evaluationMapper.toEntity(request);
        Evaluation saved = evaluationRepository.save(entity);

        log.info("EVALUATION_CREATED evaluationId={} sectionId={} type={} weight={}",
                saved.getId(), saved.getSectionId(), saved.getType(), saved.getWeight());

        return evaluationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getAllEvaluations() {
        return evaluationRepository.findAll().stream()
                .map(evaluationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluationById(Long id) {
        return evaluationMapper.toResponse(findEntityOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsBySectionId(Long sectionId) {
        return evaluationRepository.findBySectionId(sectionId).stream()
                .map(evaluationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EvaluationResponse updateEvaluation(Long id, UpdateEvaluationRequest request) {
        Evaluation entity = findEntityOrThrow(id);

        if (CLOSED.equals(entity.getStatus())) {
            log.warn("EVALUATION_NOT_EDITABLE evaluationId={} status={}", id, entity.getStatus());
            throw new EvaluationNotEditableException(id); // RN5
        }

        assertWeightWithinLimit(entity.getSectionId(), request.getWeight(), id); // RN4

        entity.setName(request.getName());
        entity.setEvaluationDate(request.getEvaluationDate());
        entity.setMaximumScore(request.getMaximumScore());
        entity.setWeight(request.getWeight());
        Evaluation saved = evaluationRepository.save(entity);

        log.info("EVALUATION_UPDATED evaluationId={} weight={}", saved.getId(), saved.getWeight());

        return evaluationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EvaluationResponse changeStatus(Long id, ChangeEvaluationStatusRequest request) {
        Evaluation entity = findEntityOrThrow(id);
        String currentStatus = entity.getStatus();
        String newStatus = request.getStatus() == null ? null : request.getStatus().toUpperCase(java.util.Locale.ROOT);

        Set<String> allowed = ALLOWED_TRANSITIONS.get(currentStatus);
        if (newStatus == null || allowed == null || !allowed.contains(newStatus)) {
            log.warn("INVALID_STATUS_TRANSITION evaluationId={} from={} to={}", id, currentStatus, request.getStatus());
            throw new InvalidStatusTransitionException(
                    "Transición de " + currentStatus + " a " + request.getStatus() + " no permitida"); // RN6
        }

        entity.setStatus(newStatus);
        Evaluation saved = evaluationRepository.save(entity);

        log.info("EVALUATION_STATUS_CHANGED evaluationId={} from={} to={}", id, currentStatus, newStatus);

        return evaluationMapper.toResponse(saved);
    }

    private Evaluation findEntityOrThrow(Long id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("EVALUATION_NOT_FOUND evaluationId={}", id);
                    return new EvaluationNotFoundException(id);
                });
    }

    private void assertSectionActive(Long sectionId) {
        if (!sectionValidationPort.exists(sectionId)) {
            log.warn("SECTION_NOT_FOUND sectionId={}", sectionId);
            throw new SectionNotFoundException(sectionId);
        }
        if (!sectionValidationPort.isActive(sectionId)) {
            log.warn("SECTION_NOT_ACTIVE sectionId={}", sectionId);
            throw new SectionNotActiveException(sectionId);
        }
    }

    /**
     * RN4. Al actualizar (excludeEvaluationId != null) se excluye la propia
     * evaluación del total actual, para no contarla dos veces.
     */
    private void assertWeightWithinLimit(Long sectionId, BigDecimal newWeight, Long excludeEvaluationId) {
        BigDecimal currentTotal = evaluationRepository.findBySectionId(sectionId).stream()
                .filter(e -> !CANCELLED.equals(e.getStatus()))
                .filter(e -> excludeEvaluationId == null || !e.getId().equals(excludeEvaluationId))
                .map(Evaluation::getWeight)
                .filter(w -> w != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (currentTotal.add(newWeight).compareTo(WEIGHT_LIMIT) > 0) {
            log.warn("WEIGHT_LIMIT_EXCEEDED sectionId={} currentTotal={} attemptedWeight={}",
                    sectionId, currentTotal, newWeight);
            throw new WeightLimitExceededException(sectionId, currentTotal, WEIGHT_LIMIT);
        }
    }
}
