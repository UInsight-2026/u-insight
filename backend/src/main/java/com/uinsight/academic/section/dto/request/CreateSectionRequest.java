package com.uinsight.academic.section.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSectionRequest {

    @NotBlank(message = "El código de la sección es obligatorio")
    private String code;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima debe ser 1")
    private Integer capacity;

    @NotNull(message = "El ID del periodo académico es obligatorio")
    private Long academicPeriodId;

    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;

    @NotNull(message = "El ID del profesor es obligatorio")
    private Long teacherId;
}