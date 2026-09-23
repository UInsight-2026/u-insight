package gt.edu.uinsight.teacher.controller;

import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    private final TeacherRepository teacherRepository;

    public TeacherController(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        if (teacher.getStatus() == null || teacher.getStatus().isEmpty()) {
            teacher.setStatus("ACTIVE");
        }
        Teacher savedTeacher = teacherRepository.save(teacher);
        return new ResponseEntity<>(savedTeacher, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        return ResponseEntity.ok(teachers);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);
        if (teacher != null) {
    return ResponseEntity.ok(teacher);
    } else {
    return ResponseEntity.notFound().build();
  }
 }
}