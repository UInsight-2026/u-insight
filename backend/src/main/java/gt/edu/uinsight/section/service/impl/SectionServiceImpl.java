package gt.edu.uinsight.section.service.impl;

import gt.edu.uinsight.section.dto.SectionCreateDTO;
import gt.edu.uinsight.section.dto.SectionResponseDTO;
import gt.edu.uinsight.section.entity.Section;
import gt.edu.uinsight.section.repository.SectionRepository;
import gt.edu.uinsight.section.service.SectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;

    public SectionServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public SectionResponseDTO createSection(SectionCreateDTO dto) {
        if (sectionRepository.existsByAcademicPeriodIdAndCourseIdAndSectionCode(
                dto.getAcademicPeriodId(), dto.getCourseId(), dto.getSectionCode())) {
            throw new IllegalArgumentException("La sección ya existe para este período y curso.");
        }

        Section section = new Section();
        section.setAcademicPeriodId(dto.getAcademicPeriodId());
        section.setCourseId(dto.getCourseId());
        section.setTeacherId(dto.getTeacherId());
        section.setSectionCode(dto.getSectionCode());
        section.setStatus("ACTIVE");

        Section saved = sectionRepository.save(section);
        return mapToDTO(saved);
    }

    @Override
    public List<SectionResponseDTO> getAllSections() {
        return sectionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SectionResponseDTO getSectionById(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada con id: " + id));
        return mapToDTO(section);
    }

    @Override
    public SectionResponseDTO updateSectionStatus(Long id, String status) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sección no encontrada con id: " + id));
        section.setStatus(status);
        return mapToDTO(sectionRepository.save(section));
    }

    private SectionResponseDTO mapToDTO(Section section) {
        return new SectionResponseDTO(
                section.getId(),
                section.getAcademicPeriodId(),
                section.getCourseId(),
                section.getTeacherId(),
                section.getSectionCode(),
                section.getStatus()
        );
    }
}