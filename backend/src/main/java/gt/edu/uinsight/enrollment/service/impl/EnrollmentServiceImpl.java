package gt.edu.uinsight.enrollment.service.impl;

import gt.edu.uinsight.enrollment.dto.EnrollmentCreateDTO;
import gt.edu.uinsight.enrollment.dto.EnrollmentResponseDTO;
import gt.edu.uinsight.enrollment.entity.Enrollment;
import gt.edu.uinsight.enrollment.repository.EnrollmentRepository;
import gt.edu.uinsight.enrollment.service.EnrollmentService;
import gt.edu.uinsight.section.entity.Section;
import gt.edu.uinsight.section.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final SectionRepository sectionRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, SectionRepository sectionRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.sectionRepository = sectionRepository;
    }

    @Override
    public EnrollmentResponseDTO enrollStudent(Long sectionId, EnrollmentCreateDTO dto) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada"));

        if ("CLOSED".equalsIgnoreCase(section.getStatus())) {
            throw new IllegalStateException("No se puede inscribir en una sección cerrada.");
        }

        if (enrollmentRepository.existsBySectionIdAndStudentId(sectionId, dto.getStudentId())) {
            throw new IllegalArgumentException("El estudiante ya se encuentra inscrito en esta sección.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setSectionId(sectionId);
        enrollment.setStudentId(dto.getStudentId());
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus("ENROLLED");

        return mapToDTO(enrollmentRepository.save(enrollment));
    }

    @Override
    public List<EnrollmentResponseDTO> getEnrollmentsBySection(Long sectionId) {
        return enrollmentRepository.findBySectionId(sectionId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponseDTO updateEnrollmentStatus(Long id, String status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        enrollment.setStatus(status);
        return mapToDTO(enrollmentRepository.save(enrollment));
    }

    private EnrollmentResponseDTO mapToDTO(Enrollment enrollment) {
        return new EnrollmentResponseDTO(
                enrollment.getId(),
                enrollment.getSectionId(),
                enrollment.getStudentId(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus()
        );
    }
}