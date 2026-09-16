package com.uinsight.academic.section.service;

import com.uinsight.academic.section.dto.request.CreateSectionRequest;
import com.uinsight.academic.section.dto.response.SectionResponse;

import java.util.List;

public interface SectionService {
    SectionResponse createSection(CreateSectionRequest request);
    SectionResponse getSectionById(Long id);
    List<SectionResponse> getAllSections();
    void deleteSection(Long id);
}
