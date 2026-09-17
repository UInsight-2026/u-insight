package gt.edu.uinsight.evaluation.mapper;

import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.entity.Evaluation;

public class EvaluationMapper {
    
    public Evaluation toEntity(CreateEvaluationRequest request) {
        if (request == null) {
            return null;
        }
        
        // El estado "DRAFT" se asigna por defecto al crear, tal como dicta la HU1
        return new Evaluation(
            request.getSectionId(),
            request.getName(),
            request.getType(),
            request.getEvaluationDate(),
            request.getMaximumScore(),
            request.getWeight(),
            "DRAFT" 
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