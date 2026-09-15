package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import java.util.List;

public interface EvaluationService {
    EvaluationResponse createEvaluation(CreateEvaluationRequest request);
    List<EvaluationResponse> getAllEvaluations();
    EvaluationResponse getEvaluationById(Long id);
}
