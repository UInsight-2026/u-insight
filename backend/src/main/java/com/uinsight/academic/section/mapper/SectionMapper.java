package com.uinsight.academic.section.mapper;

import com.uinsight.academic.section.dto.request.CreateSectionRequest;
import com.uinsight.academic.section.dto.response.SectionResponse;
import com.uinsight.academic.section.entity.Section;
import org.springframework.stereotype.Component;

@Component
public class SectionMapper {

    public Section toEntity(CreateSectionRequest request) {
        if (request == null) return null;

        return Section.builder()
                .code(request.getCode())
                .capacity(request.getCapacity())
                .academicPeriodId(request.getAcademicPeriodId())
                .courseId(request.getCourseId())
                .teacherId(request.getTeacherId())
                .build();
    }

    public SectionResponse toResponse(Section entity) {
        if (entity == null) return null;

        return SectionResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .capacity(entity.getCapacity())
                .academicPeriodId(entity.getAcademicPeriodId())
                .courseId(entity.getCourseId())
                .teacherId(entity.getTeacherId())
                .build();
    }
}