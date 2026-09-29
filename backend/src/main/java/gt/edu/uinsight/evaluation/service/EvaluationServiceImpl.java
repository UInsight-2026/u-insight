// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.api.EvaluationQueryService;
import gt.edu.uinsight.evaluation.api.EvaluationSummary;
import gt.edu.uinsight.evaluation.domain.EvaluationStatus;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Lógica de negocio del módulo de evaluaciones (célula A5).
 *
 * RN1 la evaluación pertenece a una sección existente y ACTIVE (crear, actualizar, activar).
 * RN2 maximumScore > 0 y RN3 0 < weight <= 100 (Bean Validation en los DTOs).
 * RN4 la suma de ponderaciones no canceladas de la sección no supera 100.
 * RN5 una evaluación CLOSED no se modifica; HU3 tampoco permite editar CANCELLED.
 * RN6 transiciones DRAFT->ACTIVE->CLOSED o DRAFT/ACTIVE->CANCELLED.
 */
@Service
public class EvaluationServiceImpl implements EvaluationService, EvaluationQueryService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    static final BigDecimal WEIGHT_LIMIT = new BigDecimal("100.00");

    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final SectionValidationPort sectionValidationPort;
    private final GradeEvaluationSyncPort gradeEvaluationSyncPort;

    public EvaluationServiceImpl(EvaluationRepository evaluationRepository,
                                 EvaluationMapper evaluationMapper,
                                 SectionValidationPort sectionValidationPort,
                                 GradeEvaluationSyncPort gradeEvaluationSyncPort) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
        this.sectionValidationPort = sectionValidationPort;
        this.gradeEvaluationSyncPort = gradeEvaluationSyncPort;
    }

    // ------------------------------------------------------------ HU1

    @Override
    @Transactional
    public EvaluationResponse createEvaluation(CreateEvaluationRequest request) {
        long start = System.currentTimeMillis();
        assertSectionActive(request.getSectionId());                                // RN1
        assertWeightWithinLimit(request.getSectionId(), request.getWeight(), null); // RN4

        Evaluation saved = evaluationRepository.save(evaluationMapper.toEntity(request));

        log.info("EVALUATION_CREATED operation=CREATE evaluationId={} sectionId={} type={} weight={} status={} durationMs={}",
                saved.getId(), saved.getSectionId(), saved.getType(), saved.getWeight(), saved.getStatus(),
                System.currentTimeMillis() - start);
        return evaluationMapper.toResponse(saved);
    }

    // ------------------------------------------------------------ Consultas

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getAllEvaluations() {
        return evaluationRepository.findAll().stream()
                .map(evaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluationById(Long id) {
        return evaluationMapper.toResponse(findEntityOrThrow(id));
    }

    /** HU2: 404 si la sección no existe; 200 con lista vacía si existe y no tiene evaluaciones. */
    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsBySectionId(Long sectionId) {
        assertSectionExists(sectionId);
        return evaluationRepository.findBySectionId(sectionId).stream()
                .map(evaluationMapper::toResponse)
                .toList();
    }

    // ------------------------------------------------------------ HU3

    @Override
    @Transactional
    public EvaluationResponse updateEvaluation(Long id, UpdateEvaluationRequest request) {
        long start = System.currentTimeMillis();
        Evaluation entity = findEntityOrThrow(id);
        EvaluationStatus status = statusOf(entity);

        if (!status.isEditable()) {                                                  // RN5 / HU3
            log.warn("EVALUATION_NOT_EDITABLE operation=UPDATE evaluationId={} status={}", id, status);
            throw new EvaluationNotEditableException(id, status.name());
        }
        assertSectionActive(entity.getSectionId());                                  // RN1
        assertWeightWithinLimit(entity.getSectionId(), request.getWeight(), id);     // RN4

        entity.setName(request.getName().trim());
        entity.setEvaluationDate(request.getEvaluationDate());
        entity.setMaximumScore(request.getMaximumScore());
        entity.setWeight(request.getWeight());
        Evaluation saved = evaluationRepository.save(entity);

        if (status == EvaluationStatus.ACTIVE) {
            gradeEvaluationSyncPort.publish(toSummary(saved));                      // integración A6
        }
        log.info("EVALUATION_UPDATED operation=UPDATE evaluationId={} sectionId={} weight={} maximumScore={} durationMs={}",
                saved.getId(), saved.getSectionId(), saved.getWeight(), saved.getMaximumScore(),
                System.currentTimeMillis() - start);
        return evaluationMapper.toResponse(saved);
    }

    // ------------------------------------------------------------ HU4

    @Override
    @Transactional
    public EvaluationResponse changeStatus(Long id, ChangeEvaluationStatusRequest request) {
        long start = System.currentTimeMillis();
        Evaluation entity = findEntityOrThrow(id);
        EvaluationStatus current = statusOf(entity);
        EvaluationStatus target = EvaluationStatus.valueOf(request.getStatus().trim().toUpperCase(Locale.ROOT));

        if (!current.canTransitionTo(target)) {                                      // RN6
            log.warn("INVALID_STATUS_TRANSITION operation=CHANGE_STATUS evaluationId={} from={} to={}",
                    id, current, target);
            throw new InvalidStatusTransitionException(
                    "No se permite cambiar la evaluación " + id + " de " + current + " a " + target
                            + ". Transiciones válidas desde " + current + ": " + current.allowedNext());
        }
        if (target == EvaluationStatus.ACTIVE) {
            assertSectionActive(entity.getSectionId());                              // RN1
        }

        entity.setStatus(target.name());
        Evaluation saved = evaluationRepository.save(entity);

        if (target == EvaluationStatus.ACTIVE) {
            gradeEvaluationSyncPort.publish(toSummary(saved));                      // integración A6
        }
        log.info("EVALUATION_STATUS_CHANGED operation=CHANGE_STATUS evaluationId={} from={} to={} durationMs={}",
                id, current, target, System.currentTimeMillis() - start);
        return evaluationMapper.toResponse(saved);
    }

    // ------------------------------------------------------------ API pública para otras células

    @Override
    @Transactional(readOnly = true)
    public Optional<EvaluationSummary> findSummary(Long evaluationId) {
        return evaluationRepository.findById(evaluationId).map(this::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isGradable(Long evaluationId) {
        return findSummary(evaluationId).map(EvaluationSummary::isGradable).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationSummary> findBySection(Long sectionId) {
        return evaluationRepository.findBySectionId(sectionId).stream()
                .map(this::toSummary)
                .toList();
    }

    // ------------------------------------------------------------ Reglas auxiliares

    private Evaluation findEntityOrThrow(Long id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("EVALUATION_NOT_FOUND evaluationId={}", id);
                    return new EvaluationNotFoundException(id);
                });
    }

    private void assertSectionExists(Long sectionId) {
        if (!sectionValidationPort.exists(sectionId)) {
            log.warn("SECTION_NOT_FOUND sectionId={}", sectionId);
            throw new SectionNotFoundException(sectionId);
        }
    }

    private void assertSectionActive(Long sectionId) {
        assertSectionExists(sectionId);
        if (!sectionValidationPort.isActive(sectionId)) {
            log.warn("SECTION_NOT_ACTIVE sectionId={}", sectionId);
            throw new SectionNotActiveException(sectionId);
        }
    }

    /**
     * RN4. Suma las ponderaciones de la sección que no están CANCELLED.
     * Al actualizar (excludeEvaluationId != null) se excluye la propia evaluación.
     */
    private void assertWeightWithinLimit(Long sectionId, BigDecimal newWeight, Long excludeEvaluationId) {
        BigDecimal weightToAdd = newWeight == null ? BigDecimal.ZERO : newWeight;

        BigDecimal currentTotal = evaluationRepository.findBySectionId(sectionId).stream()
                .filter(e -> !EvaluationStatus.CANCELLED.name().equals(e.getStatus()))
                .filter(e -> excludeEvaluationId == null || !Objects.equals(e.getId(), excludeEvaluationId))
                .map(Evaluation::getWeight)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (currentTotal.add(weightToAdd).compareTo(WEIGHT_LIMIT) > 0) {
            log.warn("WEIGHT_LIMIT_EXCEEDED sectionId={} currentTotal={} attemptedWeight={} limit={}",
                    sectionId, currentTotal, weightToAdd, WEIGHT_LIMIT);
            throw new WeightLimitExceededException(sectionId, currentTotal, weightToAdd, WEIGHT_LIMIT);
        }
    }

    private static EvaluationStatus statusOf(Evaluation entity) {
        return EvaluationStatus.valueOf(entity.getStatus().trim().toUpperCase(Locale.ROOT));
    }

    private EvaluationSummary toSummary(Evaluation e) {
        return new EvaluationSummary(e.getId(), e.getSectionId(), e.getName(),
                e.getMaximumScore(), e.getWeight(), e.getStatus());
    }
}
