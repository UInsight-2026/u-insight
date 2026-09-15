package gt.edu.uinsight.intervention.mapper;

import gt.edu.uinsight.intervention.dto.request.CreateInterventionRequest;
import gt.edu.uinsight.intervention.dto.response.InterventionResponse;
import gt.edu.uinsight.intervention.entity.Intervention;

/**
 * Conversión manual (sin MapStruct) entre {@code Intervention} y sus DTOs.
 */
public final class InterventionMapper {

    private InterventionMapper() {
    }

    public static Intervention toEntity(Long alertId, CreateInterventionRequest request) {
        return new Intervention(
                alertId,
                request.type(),
                request.description(),
                request.responsible(),
                request.startDate()
        );
    }

    public static InterventionResponse toResponse(Intervention entity) {
        return new InterventionResponse(
                entity.getId(),
                entity.getAlertId(),
                entity.getType(),
                entity.getDescription(),
                entity.getResponsible(),
                entity.getStartDate(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
