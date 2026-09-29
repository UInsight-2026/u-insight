package gt.edu.uinsight.grade.service;

import gt.edu.uinsight.grade.dto.GradeRequest;
import gt.edu.uinsight.grade.dto.GradeResponse;
import gt.edu.uinsight.grade.exception.DuplicateGradeException;
import gt.edu.uinsight.grade.exception.InvalidRequestException;
import gt.edu.uinsight.grade.exception.InvalidScoreException;
import gt.edu.uinsight.grade.exception.ResourceNotFoundException;
import gt.edu.uinsight.grade.exception.StudentNotEnrolledException;
import gt.edu.uinsight.grade.model.EvaluationRef;
import gt.edu.uinsight.grade.model.Grade;
import gt.edu.uinsight.grade.model.GradeStatus;
import gt.edu.uinsight.grade.repository.EnrollmentRefRepository;
import gt.edu.uinsight.grade.repository.EvaluationRefRepository;
import gt.edu.uinsight.grade.repository.GradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registra una calificacion en su propia transaccion (REQUIRES_NEW) para
 * que, en un lote, el fallo de un item no revierta los items ya registrados.
 */
@Component
class GradeRegistrar {

    private static final Logger log = LoggerFactory.getLogger(GradeRegistrar.class);

    private final GradeRepository gradeRepository;
    private final EvaluationRefRepository evaluationRefRepository;
    private final EnrollmentRefRepository enrollmentRefRepository;

    GradeRegistrar(GradeRepository gradeRepository, EvaluationRefRepository evaluationRefRepository,
                    EnrollmentRefRepository enrollmentRefRepository) {
        this.gradeRepository = gradeRepository;
        this.evaluationRefRepository = evaluationRefRepository;
        this.enrollmentRefRepository = enrollmentRefRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GradeResponse register(GradeRequest request) {
        if (request.getEvaluationId() == null || request.getStudentId() == null || request.getScore() == null) {
            throw new InvalidRequestException("evaluationId, studentId y score son obligatorios");
        }
        if (request.getScore().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidScoreException("score no puede ser negativo");
        }

        EvaluationRef evaluation = evaluationRefRepository.findById(request.getEvaluationId())
            .orElseThrow(() -> new ResourceNotFoundException("La evaluacion no existe: " + request.getEvaluationId()));

        if (!enrollmentRefRepository.existsByStudentIdAndSectionId(request.getStudentId(), evaluation.getSectionId())) {
            throw new StudentNotEnrolledException(
                "El estudiante " + request.getStudentId() + " no esta inscrito en la seccion " + evaluation.getSectionId());
        }

        validateScoreRange(request.getScore(), evaluation.getMaximumScore());

        if (gradeRepository.existsByEvaluationIdAndStudentId(request.getEvaluationId(), request.getStudentId())) {
            throw new DuplicateGradeException(
                "Ya existe una calificacion para el estudiante " + request.getStudentId()
                    + " en la evaluacion " + request.getEvaluationId());
        }

        Grade grade = new Grade(request.getEvaluationId(), request.getStudentId(), request.getScore(),
            LocalDateTime.now(), GradeStatus.REGISTERED);
        grade = gradeRepository.save(grade);

        log.info("GRADE_REGISTERED gradeId={} evaluationId={} studentId={} score={}",
            grade.getId(), grade.getEvaluationId(), grade.getStudentId(), grade.getScore());
        return GradeResponse.fromEntity(grade);
    }

    static void validateScoreRange(BigDecimal score, BigDecimal maximumScore) {
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(maximumScore) > 0) {
            throw new InvalidScoreException("El score debe estar entre 0 y " + maximumScore);
        }
    }
}
