package com.uinsight.academic.section.controller;

import com.uinsight.academic.section.dto.request.CreateSectionRequest;
import com.uinsight.academic.section.dto.response.SectionResponse;
import com.uinsight.academic.section.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sections")
@RequiredArgsConstructor
@Tag(name = "Sections", description = "Endpoints para la gestión de secciones (Célula A4)")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    @Operation(summary = "Crear una nueva sección", description = "Registra una sección asociada a un período académico, curso y profesor.")
    public ResponseEntity<SectionResponse> createSection(@Valid @RequestBody CreateSectionRequest request) {
        SectionResponse response = sectionService.createSection(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sección por ID", description = "Retorna el detalle completo de una sección a partir de su ID.")
    public ResponseEntity<SectionResponse> getSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas las secciones", description = "Retorna el listado completo de secciones registradas.")
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        return ResponseEntity.ok(sectionService.getAllSections());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sección por ID", description = "Remueve una sección del sistema según su ID.")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }
}