package com.uinsight.academic.enrollment.controller;

import com.uinsight.academic.enrollment.dto.request.EnrollmentRequestDTO;
import com.uinsight.academic.enrollment.dto.response.EnrollmentResponseDTO;
import com.uinsight.academic.enrollment.entity.Enrollment;
import com.uinsight.academic.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Endpoints para la gestión de inscripciones (Célula A4)")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @Operation(summary = "Listar todas las inscripciones")
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllEnrollments() {
        List<EnrollmentResponseDTO> response = enrollmentService.getAllEnrollments().stream()
                .map(EnrollmentResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva inscripción con validación de cupo")
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(@Valid @RequestBody EnrollmentRequestDTO request) {
        Enrollment enrollment = enrollmentService.enrollStudent(request.getStudentId(), request.getSectionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(EnrollmentResponseDTO.fromEntity(enrollment));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Obtener inscripciones de un estudiante específico")
    public ResponseEntity<List<EnrollmentResponseDTO>> getByStudent(@PathVariable Long studentId) {
        List<EnrollmentResponseDTO> response = enrollmentService.getEnrollmentsByStudent(studentId).stream()
                .map(EnrollmentResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/section/{sectionId}")
    @Operation(summary = "Obtener inscripciones de una sección específica")
    public ResponseEntity<List<EnrollmentResponseDTO>> getBySection(@PathVariable Long sectionId) {
        List<EnrollmentResponseDTO> response = enrollmentService.getEnrollmentsBySection(sectionId).stream()
                .map(EnrollmentResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar una inscripción existente")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}