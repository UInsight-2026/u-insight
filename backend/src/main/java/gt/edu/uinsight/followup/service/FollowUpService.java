// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.service;

import gt.edu.uinsight.followup.dto.request.CreateFollowUpRequest;
import gt.edu.uinsight.followup.dto.response.FollowUpResponse;
import gt.edu.uinsight.followup.entity.FollowUp;
import gt.edu.uinsight.followup.exception.InterventionNotActiveException;
import gt.edu.uinsight.followup.exception.InvalidFollowUpDateException;
import gt.edu.uinsight.followup.mapper.FollowUpMapper;
import gt.edu.uinsight.followup.repository.FollowUpRepository;
import gt.edu.uinsight.intervention.entity.Intervention;
import gt.edu.uinsight.intervention.entity.InterventionStatus;
import gt.edu.uinsight.intervention.exception.InterventionNotFoundException;
import gt.edu.uinsight.intervention.repository.InterventionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Orquesta la lógica de negocio del módulo de seguimiento (RN-5, RN-6, RN-7).
 */
@Service
public class FollowUpService {

    private static final Logger log = LoggerFactory.getLogger(FollowUpService.class);

    private static final Set<InterventionStatus> ACTIVE_STATUSES =
            Set.of(InterventionStatus.PLANNED, InterventionStatus.IN_PROGRESS);

    private final FollowUpRepository followUpRepository;
    private final InterventionRepository interventionRepository;

    public FollowUpService(FollowUpRepository followUpRepository,
                            InterventionRepository interventionRepository) {
        this.followUpRepository = followUpRepository;
        this.interventionRepository = interventionRepository;
    }

    @Transactional
    public FollowUpResponse create(Long interventionId, CreateFollowUpRequest request) {
        Intervention intervention = findIntervention(interventionId);
        assertInterventionActive(intervention);
        assertFollowUpDateValid(intervention, request.followUpDate());

        FollowUp entity = FollowUpMapper.toEntity(interventionId, request);
        FollowUp saved = followUpRepository.save(entity);

        log.info("FOLLOWUP_REGISTERED followUpId={} interventionId={}", saved.getId(), interventionId);

        return FollowUpMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FollowUpResponse> listByIntervention(Long interventionId) {
        findIntervention(interventionId);

        List<FollowUp> followUps =
                followUpRepository.findByInterventionIdAndDeletedFalseOrderByFollowUpDateAsc(interventionId);

        return FollowUpMapper.toResponseList(followUps);
    }

    private Intervention findIntervention(Long interventionId) {
        return interventionRepository.findById(interventionId)
                .orElseThrow(() -> {
                    log.warn("INTERVENTION_NOT_FOUND interventionId={}", interventionId);
                    return new InterventionNotFoundException(interventionId);
                });
    }

    private void assertInterventionActive(Intervention intervention) {
        if (!ACTIVE_STATUSES.contains(intervention.getStatus())) {
            log.warn("INTERVENTION_NOT_ACTIVE interventionId={} status={}",
                    intervention.getId(), intervention.getStatus());
            throw new InterventionNotActiveException(intervention.getId());
        }
    }

    private void assertFollowUpDateValid(Intervention intervention, java.time.LocalDate followUpDate) {
        if (followUpDate.isBefore(intervention.getStartDate())) {
            throw new InvalidFollowUpDateException(followUpDate, intervention.getStartDate());
        }
    }
}
