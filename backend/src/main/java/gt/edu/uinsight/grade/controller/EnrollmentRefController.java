package gt.edu.uinsight.grade.controller;

import gt.edu.uinsight.grade.dto.EnrollmentRefRequest;
import gt.edu.uinsight.grade.model.EnrollmentRef;
import gt.edu.uinsight.grade.repository.EnrollmentRefRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint de apoyo del modulo de calificaciones (celula A6): permite cargar
 * los datos de referencia de Matriculas que este modulo necesita para
 * validar que el estudiante pertenece a la seccion, mientras la celula A4
 * no este integrada. No reemplaza la futura API real de Matriculas de A4 --
 * por eso vive bajo /api/v1/grades/... y no bajo /api/v1/enrollments.
 */
@RestController
@RequestMapping("/api/v1/grades/enrollment-refs")
@CrossOrigin(origins = "*")
public class EnrollmentRefController {

    private final EnrollmentRefRepository enrollmentRefRepository;

    public EnrollmentRefController(EnrollmentRefRepository enrollmentRefRepository) {
        this.enrollmentRefRepository = enrollmentRefRepository;
    }

    @PostMapping
    public ResponseEntity<EnrollmentRef> create(@RequestBody EnrollmentRefRequest request) {
        EnrollmentRef enrollment = new EnrollmentRef(request.getStudentId(), request.getSectionId());
        EnrollmentRef saved = enrollmentRefRepository.save(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentRef>> getAll() {
        return ResponseEntity.ok(enrollmentRefRepository.findAll());
    }
}
