package gt.edu.uinsight.enrollment.service;

import gt.edu.uinsight.enrollment.dto.EnrollmentCreateDTO;
import gt.edu.uinsight.enrollment.dto.EnrollmentResponseDTO;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponseDTO enrollStudent(Long sectionId, EnrollmentCreateDTO dto);
    List<EnrollmentResponseDTO> getEnrollmentsBySection(Long sectionId);
    EnrollmentResponseDTO updateEnrollmentStatus(Long id, String status);
}