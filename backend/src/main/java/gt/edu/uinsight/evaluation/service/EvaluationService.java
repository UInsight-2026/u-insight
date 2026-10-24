package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.dto.request.ChangeEvaluationStatusRequest;
import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.request.UpdateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import java.util.List;

public interface EvaluationService {
    EvaluationResponse createEvaluation(CreateEvaluationRequest request);
    List<EvaluationResponse> getAllEvaluations();
    EvaluationResponse getEvaluationById(Long id);
    EvaluationResponse updateEvaluation(Long id, UpdateEvaluationRequest request);
    List<EvaluationResponse> getEvaluationsBySectionId(Long sectionId);
    EvaluationResponse changeStatus(Long id, ChangeEvaluationStatusRequest request);
}