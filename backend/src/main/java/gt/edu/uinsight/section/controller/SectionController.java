package gt.edu.uinsight.section.controller;

import gt.edu.uinsight.section.dto.SectionCreateDTO;
import gt.edu.uinsight.section.dto.SectionResponseDTO;
import gt.edu.uinsight.section.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<SectionResponseDTO> createSection(@Valid @RequestBody SectionCreateDTO dto) {
        return new ResponseEntity<>(sectionService.createSection(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SectionResponseDTO>> getAllSections() {
        return ResponseEntity.ok(sectionService.getAllSections());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionResponseDTO> getSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SectionResponseDTO> updateSectionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(sectionService.updateSectionStatus(id, status));
    }
}