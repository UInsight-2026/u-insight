package com.uinsight.academic.section.service.impl;

import com.uinsight.academic.section.dto.request.CreateSectionRequest;
import com.uinsight.academic.section.dto.response.SectionResponse;
import com.uinsight.academic.section.entity.Section;
import com.uinsight.academic.section.mapper.SectionMapper;
import com.uinsight.academic.section.repository.SectionRepository;
import com.uinsight.academic.section.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;

    @Override
    @Transactional
    public SectionResponse createSection(CreateSectionRequest request) {
        if (sectionRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Ya existe una sección registrada con el código: " + request.getCode());
        }

        Section section = sectionMapper.toEntity(request);
        Section savedSection = sectionRepository.save(section);
        return sectionMapper.toResponse(savedSection);
    }

    @Override
    @Transactional(readOnly = true)
    public SectionResponse getSectionById(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada con ID: " + id));
        return sectionMapper.toResponse(section);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SectionResponse> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(sectionMapper::toResponse)
                //.toList();
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSection(Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Sección no encontrada con ID: " + id);
        }
        sectionRepository.deleteById(id);
    }
}