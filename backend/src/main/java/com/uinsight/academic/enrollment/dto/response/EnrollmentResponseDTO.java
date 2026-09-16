package com.uinsight.academic.enrollment.dto.response;

import com.uinsight.academic.enrollment.entity.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDTO {

    private Long id;
    private Long studentId;
    private Long sectionId;
    private LocalDateTime enrollmentDate;
    private String status;

    public static EnrollmentResponseDTO fromEntity(Enrollment entity) {
        return EnrollmentResponseDTO.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .sectionId(entity.getSectionId())
                .enrollmentDate(entity.getEnrollmentDate())
                .status(entity.getStatus())
                .build();
    }
}