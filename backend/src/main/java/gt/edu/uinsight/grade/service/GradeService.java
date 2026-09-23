package gt.edu.uinsight.grade.service;

import gt.edu.uinsight.grade.dto.BatchGradeResult;
import gt.edu.uinsight.grade.dto.BatchItemResult;
import gt.edu.uinsight.grade.dto.GradeRequest;
import gt.edu.uinsight.grade.dto.GradeResponse;
import gt.edu.uinsight.grade.dto.GradeUpdateRequest;
import gt.edu.uinsight.grade.exception.InvalidRequestException;
import gt.edu.uinsight.grade.exception.ResourceNotFoundException;
import gt.edu.uinsight.grade.model.EvaluationRef;
import gt.edu.uinsight.grade.model.Grade;
import gt.edu.uinsight.grade.model.GradeStatus;
import gt.edu.uinsight.grade.repository.EvaluationRefRepository;
import gt.edu.uinsight.grade.repository.GradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GradeService {

    private static final Logger log = LoggerFactory.getLogger(GradeService.class);

    private final GradeRepository gradeRepository;
    private final EvaluationRefRepository evaluationRefRepository;
    private final GradeRegistrar gradeRegistrar;

    public GradeService(GradeRepository gradeRepository, EvaluationRefRepository evaluationRefRepository,
                         GradeRegistrar gradeRegistrar) {
        this.gradeRepository = gradeRepository;
        this.evaluationRefRepository = evaluationRefRepository;
        this.gradeRegistrar = gradeRegistrar;
    }

    public GradeResponse registerGrade(GradeRequest request) {
        return gradeRegistrar.register(request);
    }

    public BatchGradeResult registerBatch(List<GradeRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new InvalidRequestException("El lote debe contener al menos una calificacion");
        }

        log.info("GRADE_BATCH_STARTED totalRequested={}", requests.size());
        List<BatchItemResult> results = new ArrayList<>();
        int registered = 0;

        for (int i = 0; i < requests.size(); i++) {
            GradeRequest request = requests.get(i);
            try {
                GradeResponse response = gradeRegistrar.register(request);
                results.add(BatchItemResult.success(i, request.getEvaluationId(), request.getStudentId(), response.getId()));
                registered++;
            } catch (RuntimeException ex) {
                log.warn("GRADE_REJECTED index={} evaluationId={} studentId={} reason={}",
                    i, request.getEvaluationId(), request.getStudentId(), ex.getMessage());
                results.add(BatchItemResult.failure(i, request.getEvaluationId(), request.getStudentId(), ex.getMessage()));
            }
        }

        int rejected = requests.size() - registered;
        log.info("GRADE_BATCH_COMPLETED totalRequested={} totalRegistered={} totalRejected={}",
            requests.size(), registered, rejected);
        return new BatchGradeResult(requests.size(), registered, rejected, results);
    }

    public GradeResponse getById(Long id) {
        Grade grade = gradeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Calificacion no encontrada: " + id));
        return GradeResponse.fromEntity(grade);
    }

    public List<GradeResponse> getByEvaluation(Long evaluationId) {
        return gradeRepository.findByEvaluationId(evaluationId).stream()
            .map(GradeResponse::fromEntity)
            .toList();
    }

    public List<GradeResponse> getByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream()
            .map(GradeResponse::fromEntity)
            .toList();
    }

    public List<GradeResponse> getBySection(Long sectionId) {
        return gradeRepository.findBySectionId(sectionId).stream()
            .map(GradeResponse::fromEntity)
            .toList();
    }

    public GradeResponse updateGrade(Long id, GradeUpdateRequest request) {
        if (request.getScore() == null) {
            throw new InvalidRequestException("score es obligatorio");
        }

        Grade grade = gradeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Calificacion no encontrada: " + id));

        EvaluationRef evaluation = evaluationRefRepository.findById(grade.getEvaluationId())
            .orElseThrow(() -> new ResourceNotFoundException("La evaluacion no existe: " + grade.getEvaluationId()));
        GradeRegistrar.validateScoreRange(request.getScore(), evaluation.getMaximumScore());

        grade.setScore(request.getScore());
        grade.setStatus(GradeStatus.UPDATED);
        Grade updated = gradeRepository.save(grade);

        log.info("GRADE_UPDATED gradeId={} evaluationId={} studentId={} score={}",
            updated.getId(), updated.getEvaluationId(), updated.getStudentId(), updated.getScore());
        return GradeResponse.fromEntity(updated);
    }
}
