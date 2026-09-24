package gt.edu.uinsight.grade.controller;

import gt.edu.uinsight.grade.dto.EvaluationRefRequest;
import gt.edu.uinsight.grade.exception.InvalidRequestException;
import gt.edu.uinsight.grade.model.EvaluationRef;
import gt.edu.uinsight.grade.repository.EvaluationRefRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint de apoyo del modulo de calificaciones (celula A6): permite cargar
 * los datos de referencia de Evaluaciones que este modulo necesita para
 * validar (existencia, seccion, nota maxima) mientras la celula A5 no este
 * integrada. No reemplaza la futura API real de Evaluaciones de A5 -- por
 * eso vive bajo /api/v1/grades/... y no bajo /api/v1/evaluations.
 */
@RestController
@RequestMapping("/api/v1/grades/evaluation-refs")
@CrossOrigin(origins = "*")
public class EvaluationRefController {

    private final EvaluationRefRepository evaluationRefRepository;

    public EvaluationRefController(EvaluationRefRepository evaluationRefRepository) {
        this.evaluationRefRepository = evaluationRefRepository;
    }

    @PostMapping
    public ResponseEntity<EvaluationRef> create(@RequestBody EvaluationRefRequest request) {
        if (request.getId() == null) {
            throw new InvalidRequestException(
                "id es obligatorio: debe coincidir con el id real de la evaluacion (celula A5)");
        }
        boolean isUpdate = evaluationRefRepository.existsById(request.getId());

        EvaluationRef evaluation = new EvaluationRef(request.getId(), request.getSectionId(),
            request.getName(), request.getMaximumScore());
        EvaluationRef saved = evaluationRefRepository.save(evaluation);

        HttpStatus status = isUpdate ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<EvaluationRef>> getAll() {
        return ResponseEntity.ok(evaluationRefRepository.findAll());
    }
}
