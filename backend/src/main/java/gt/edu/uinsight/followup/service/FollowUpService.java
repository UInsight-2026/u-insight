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
 * Orquesta la lógica de negocio del módulo de seguimiento (follow-up): RN-5,
 * RN-6, RN-7.
 */
@Service
public class FollowUpService {

    private static final Logger log = LoggerFactory.getLogger(FollowUpService.class);

    private static final Set<InterventionStatus> SEGUIMIENTO_PERMITIDO =
            Set.of(InterventionStatus.PLANNED, InterventionStatus.IN_PROGRESS);

    private final FollowUpRepository followUpRepository;
    private final InterventionRepository interventionRepository;

    public FollowUpService(FollowUpRepository followUpRepository, InterventionRepository interventionRepository) {
        this.followUpRepository = followUpRepository;
        this.interventionRepository = interventionRepository;
    }

    @Transactional
    public FollowUpResponse create(Long interventionId, CreateFollowUpRequest request) {
        Intervention intervention = getIntervention(interventionId);

        if (!SEGUIMIENTO_PERMITIDO.contains(intervention.getStatus())) {
            log.warn("FOLLOW_UP_REJECTED interventionId={} reason=INTERVENTION_NOT_ACTIVE status={}",
                    interventionId, intervention.getStatus());
            throw new InterventionNotActiveException(interventionId);
        }

        if (request.followUpDate().isBefore(intervention.getStartDate())) {
            log.warn("FOLLOW_UP_REJECTED interventionId={} reason=DATE_BEFORE_START followUpDate={} startDate={}",
                    interventionId, request.followUpDate(), intervention.getStartDate());
            throw new InvalidFollowUpDateException(
                    "followUpDate no puede ser anterior a la fecha de inicio de la intervención ("
                            + intervention.getStartDate() + ")");
        }

        FollowUp entity = FollowUpMapper.toEntity(interventionId, request);
        FollowUp saved = followUpRepository.save(entity);

        log.info("FOLLOW_UP_REGISTERED followUpId={} interventionId={}", saved.getId(), interventionId);

        return FollowUpMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FollowUpResponse> listByIntervention(Long interventionId) {
        getIntervention(interventionId);

        return FollowUpMapper.toResponseList(
                followUpRepository.findByInterventionIdAndDeletedFalseOrderByFollowUpDateAsc(interventionId));
    }

    private Intervention getIntervention(Long interventionId) {
        return interventionRepository.findById(interventionId)
                .orElseThrow(() -> {
                    log.warn("INTERVENTION_NOT_FOUND interventionId={}", interventionId);
                    return new InterventionNotFoundException(interventionId);
                });
    }
}
