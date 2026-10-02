package gt.edu.uinsight.enrollment.controller;

import gt.edu.uinsight.enrollment.dto.EnrollmentCreateDTO;
import gt.edu.uinsight.enrollment.dto.EnrollmentResponseDTO;
import gt.edu.uinsight.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/sections/{sectionId}/enrollments")
    public ResponseEntity<EnrollmentResponseDTO> enrollStudent(
            @PathVariable Long sectionId,
            @Valid @RequestBody EnrollmentCreateDTO dto) {
        return new ResponseEntity<>(enrollmentService.enrollStudent(sectionId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/sections/{sectionId}/enrollments")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsBySection(@PathVariable Long sectionId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsBySection(sectionId));
    }

    @PatchMapping("/enrollments/{id}/status")
    public ResponseEntity<EnrollmentResponseDTO> updateEnrollmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(id, status));
    }
}