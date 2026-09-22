package com.uinsight.academic.enrollment.service.impl;

import com.uinsight.academic.enrollment.entity.Enrollment;
import com.uinsight.academic.enrollment.repository.EnrollmentRepository;
import com.uinsight.academic.enrollment.service.EnrollmentService;
import com.uinsight.academic.section.entity.Section;
import com.uinsight.academic.section.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final SectionRepository sectionRepository;

    @Override
    @Transactional
    public Enrollment enrollStudent(Long studentId, Long sectionId) {
        // 1. Validar existencia de la sección
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("La sección solicitada no existe con ID: " + sectionId));

        // 2. Validar que el estudiante no esté ya inscrito en esta sección
        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndSectionIdAndStatus(studentId, sectionId, "ACTIVE");
        if (alreadyEnrolled) {
            throw new IllegalStateException("El estudiante ya se encuentra inscrito en esta sección.");
        }

        // 3. Validar cupo disponible en la sección
        long activeEnrollments = enrollmentRepository.findBySectionId(sectionId).stream()
                .filter(e -> "ACTIVE".equals(e.getStatus()))
                .count();

        if (activeEnrollments >= section.getCapacity()) {
            throw new IllegalStateException("La sección ha alcanzado su capacidad máxima (" + section.getCapacity() + " estudiantes).");
        }

        // 4. Crear e insertar la nueva inscripción
        Enrollment newEnrollment = Enrollment.builder()
                .studentId(studentId)
                .sectionId(sectionId)
                .status("ACTIVE")
                .build();

        return enrollmentRepository.save(newEnrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsBySection(Long sectionId) {
        return enrollmentRepository.findBySectionId(sectionId);
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la inscripción con ID: " + enrollmentId));

        enrollment.setStatus("CANCELLED");
        enrollmentRepository.save(enrollment);
    }
}