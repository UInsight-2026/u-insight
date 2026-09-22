package com.uinsight.academic.enrollment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestDTO {

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;

    @NotNull(message = "El ID de la sección es obligatorio")
    private Long sectionId;
}