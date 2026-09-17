package com.uinsight.academic.section.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectionResponse {

    private Long id;
    private String code;
    private Integer capacity;
    private Long academicPeriodId;
    private Long courseId;
    private Long teacherId;
}