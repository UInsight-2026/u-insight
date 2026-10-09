// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.mapper;

import gt.edu.uinsight.evaluation.domain.EvaluationStatus;
import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.entity.Evaluation;
import org.springframework.stereotype.Component;

import java.util.Locale;

/** Conversión entre DTOs y la entidad Evaluation. Nunca se expone la entidad en la API. */
@Component
public class EvaluationMapper {

    /** Toda evaluación nueva nace en DRAFT; el tipo se normaliza a mayúsculas. */
    public Evaluation toEntity(CreateEvaluationRequest request) {
        if (request == null) {
            return null;
        }
        return new Evaluation(
                request.getSectionId(),
                request.getName().trim(),
                request.getType().trim().toUpperCase(Locale.ROOT),
                request.getEvaluationDate(),
                request.getMaximumScore(),
                request.getWeight(),
                EvaluationStatus.DRAFT.name()
        );
    }

    public EvaluationResponse toResponse(Evaluation entity) {
        if (entity == null) {
            return null;
        }
        return new EvaluationResponse(
                entity.getId(),
                entity.getSectionId(),
                entity.getName(),
                entity.getType(),
                entity.getEvaluationDate(),
                entity.getMaximumScore(),
                entity.getWeight(),
                entity.getStatus()
        );
    }
}
