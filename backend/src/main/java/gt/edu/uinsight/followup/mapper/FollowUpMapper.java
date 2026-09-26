// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.mapper;

import gt.edu.uinsight.followup.dto.request.CreateFollowUpRequest;
import gt.edu.uinsight.followup.dto.response.FollowUpResponse;
import gt.edu.uinsight.followup.entity.FollowUp;

import java.util.List;

/**
 * Conversión manual (sin MapStruct) entre {@code FollowUp} y sus DTOs.
 */
public final class FollowUpMapper {

    private FollowUpMapper() {
    }

    public static FollowUp toEntity(Long interventionId, CreateFollowUpRequest request) {
        return new FollowUp(
                interventionId,
                request.followUpDate(),
                request.observation(),
                request.result()
        );
    }

    public static FollowUpResponse toResponse(FollowUp entity) {
        return new FollowUpResponse(
                entity.getId(),
                entity.getInterventionId(),
                entity.getFollowUpDate(),
                entity.getObservation(),
                entity.getResult()
        );
    }

    public static List<FollowUpResponse> toResponseList(List<FollowUp> entities) {
        return entities.stream()
                .map(FollowUpMapper::toResponse)
                .toList();
    }
}
