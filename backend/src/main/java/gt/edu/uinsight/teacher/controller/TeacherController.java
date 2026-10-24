package gt.edu.uinsight.teacher.controller;

import gt.edu.uinsight.teacher.dto.TeacherRequest;
import gt.edu.uinsight.teacher.dto.TeacherSectionResponse;
import gt.edu.uinsight.teacher.dto.TeacherStatusRequest;
import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody TeacherRequest request) {
        return new ResponseEntity<>(teacherService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id,
                                                 @Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(teacherService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Teacher> updateTeacherStatus(@PathVariable Long id,
                                                       @Valid @RequestBody TeacherStatusRequest request) {
        return ResponseEntity.ok(teacherService.changeStatus(id, request.getStatus()));
    }

    @GetMapping("/{id}/sections")
    public ResponseEntity<List<TeacherSectionResponse>> getTeacherSections(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.findSectionsByTeacher(id));
    }

    @PutMapping("/{id}/sections/{sectionId}")
    public ResponseEntity<TeacherSectionResponse> assignSection(@PathVariable Long id,
                                                                @PathVariable Long sectionId) {
        return ResponseEntity.ok(teacherService.assignToSection(id, sectionId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
