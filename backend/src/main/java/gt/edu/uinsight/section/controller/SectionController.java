package gt.edu.uinsight.section.controller;

import gt.edu.uinsight.section.dto.SectionRequest;
import gt.edu.uinsight.section.dto.SectionResponse;
import gt.edu.uinsight.section.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sections")
@CrossOrigin(origins = "*")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@Valid @RequestBody SectionRequest request) {
        return new ResponseEntity<>(sectionService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        return ResponseEntity.ok(sectionService.findAll());
    }
}
