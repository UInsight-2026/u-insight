// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.service;

import gt.edu.uinsight.intervention.dto.request.CreateInterventionRequest;
import gt.edu.uinsight.intervention.dto.request.UpdateInterventionStatusRequest;
import gt.edu.uinsight.intervention.dto.response.InterventionResponse;
import gt.edu.uinsight.intervention.entity.Intervention;
import gt.edu.uinsight.intervention.entity.InterventionStatus;
import gt.edu.uinsight.intervention.exception.AlertNotActiveException;
import gt.edu.uinsight.intervention.exception.AlertNotFoundException;
import gt.edu.uinsight.intervention.exception.InterventionNotFoundException;
import gt.edu.uinsight.intervention.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.intervention.mapper.InterventionMapper;
import gt.edu.uinsight.intervention.repository.InterventionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orquesta la lógica de negocio del módulo de intervenciones (RN-1, RN-2, RN-3, RN-7).
 */
@Service
public class InterventionService {

    private static final Logger log = LoggerFactory.getLogger(InterventionService.class);

    private static final Map<InterventionStatus, Set<InterventionStatus>> ALLOWED_TRANSITIONS =
            new EnumMap<>(InterventionStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(InterventionStatus.PLANNED,
                EnumSet.of(InterventionStatus.IN_PROGRESS, InterventionStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(InterventionStatus.IN_PROGRESS,
                EnumSet.of(InterventionStatus.COMPLETED, InterventionStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(InterventionStatus.COMPLETED, EnumSet.noneOf(InterventionStatus.class));
        ALLOWED_TRANSITIONS.put(InterventionStatus.CANCELLED, EnumSet.noneOf(InterventionStatus.class));
    }

    private final InterventionRepository interventionRepository;
    private final AlertValidationPort alertValidationPort;

    public InterventionService(InterventionRepository interventionRepository,
                                AlertValidationPort alertValidationPort) {
        this.interventionRepository = interventionRepository;
        this.alertValidationPort = alertValidationPort;
    }

    @Transactional
    public InterventionResponse create(Long alertId, CreateInterventionRequest request) {
        assertAlertExists(alertId);
        assertAlertActive(alertId);

        Intervention entity = InterventionMapper.toEntity(alertId, request);
        Intervention saved = interventionRepository.save(entity);

        log.info("INTERVENTION_REGISTERED interventionId={} alertId={} type={}",
                saved.getId(), alertId, saved.getType());

        return InterventionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InterventionResponse> findByAlert(Long alertId) {
        assertAlertExists(alertId);

        return interventionRepository.findByAlertIdOrderByStartDateAsc(alertId).stream()
                .map(InterventionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InterventionResponse findById(Long id) {
        Intervention entity = interventionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("INTERVENTION_NOT_FOUND interventionId={}", id);
                    return new InterventionNotFoundException(id);
                });
        return InterventionMapper.toResponse(entity);
    }

    @Transactional
    public InterventionResponse updateStatus(Long id, UpdateInterventionStatusRequest request) {
        Intervention entity = interventionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("INTERVENTION_NOT_FOUND interventionId={}", id);
                    return new InterventionNotFoundException(id);
                });

        InterventionStatus current = entity.getStatus();
        InterventionStatus next = request.status();

        if (!ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(next)) {
            log.warn("INTERVENTION_STATUS_TRANSITION_REJECTED interventionId={} from={} to={}", id, current, next);
            throw new InvalidStatusTransitionException(current, next);
        }

        entity.changeStatus(next);
        Intervention saved = interventionRepository.save(entity);

        log.info("INTERVENTION_STATUS_CHANGED interventionId={} from={} to={}", id, current, next);

        return InterventionMapper.toResponse(saved);
    }

    private void assertAlertExists(Long alertId) {
        if (!alertValidationPort.exists(alertId)) {
            log.warn("ALERT_NOT_FOUND alertId={}", alertId);
            throw new AlertNotFoundException(alertId);
        }
    }

    private void assertAlertActive(Long alertId) {
        if (!alertValidationPort.isActive(alertId)) {
            throw new AlertNotActiveException(alertId);
        }
    }
}
