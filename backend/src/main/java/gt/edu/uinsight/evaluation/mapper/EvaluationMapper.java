package gt.edu.uinsight.evaluation.mapper;

import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.entity.Evaluation;

public class EvaluationMapper {
    public Evaluation toEntity(CreateEvaluationRequest request) {
        if (request == null) {
            return null;
        }
        return new Evaluation(
            request.getName(),
            request.getDescription(),
            request.getCourseId(),
            request.getSectionId(),
            request.getMaxScore()
        );
    }

    public EvaluationResponse toResponse(Evaluation entity) {
        if (entity == null) {
            return null;
        }
        return new EvaluationResponse(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCourseId(),
            entity.getSectionId(),
            entity.getMaxScore(),
            entity.getCreatedAt()
        );
    }
}
