// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.service;

import gt.edu.uinsight.intervention.dto.request.CreateInterventionRequest;
import gt.edu.uinsight.intervention.dto.response.InterventionResponse;
import gt.edu.uinsight.intervention.entity.Intervention;
import gt.edu.uinsight.intervention.exception.AlertNotActiveException;
import gt.edu.uinsight.intervention.exception.AlertNotFoundException;
import gt.edu.uinsight.intervention.exception.InterventionNotFoundException;
import gt.edu.uinsight.intervention.mapper.InterventionMapper;
import gt.edu.uinsight.intervention.repository.InterventionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Orquesta la lógica de negocio del módulo de intervenciones (RN-1, RN-2, RN-3, RN-7).
 */
@Service
public class InterventionService {

    private static final Logger log = LoggerFactory.getLogger(InterventionService.class);

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
