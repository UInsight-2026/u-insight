package gt.edu.uinsight.section.service;

import gt.edu.uinsight.section.dto.SectionCreateDTO;
import gt.edu.uinsight.section.dto.SectionResponseDTO;

import java.util.List;

public interface SectionService {
    SectionResponseDTO createSection(SectionCreateDTO dto);
    List<SectionResponseDTO> getAllSections();
    SectionResponseDTO getSectionById(Long id);
    SectionResponseDTO updateSectionStatus(Long id, String status);
}