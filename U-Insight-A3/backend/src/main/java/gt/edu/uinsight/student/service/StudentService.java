package gt.edu.uinsight.student.service;

import gt.edu.uinsight.grade.entity.Grade;
import gt.edu.uinsight.grade.repository.GradeRepository;
import gt.edu.uinsight.student.dto.request.CreateStudentRequest;
import gt.edu.uinsight.student.dto.response.StudentAcademicHistoryResponse;
import gt.edu.uinsight.student.dto.response.StudentResponse;
import gt.edu.uinsight.student.entity.Student;
import gt.edu.uinsight.student.entity.StudentStatus;
import gt.edu.uinsight.student.exception.DuplicateStudentCodeException;
import gt.edu.uinsight.student.exception.InactiveStudentException;
import gt.edu.uinsight.student.exception.StudentNotFoundException;
import gt.edu.uinsight.student.mapper.StudentMapper;
import gt.edu.uinsight.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;

    public StudentService(StudentRepository studentRepository, GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
    }

    @Transactional
    public StudentResponse create(CreateStudentRequest request) {
        String code = normalizeCode(request.studentCode());
        if (studentRepository.existsByStudentCodeIgnoreCase(code)) {
            throw new DuplicateStudentCodeException(code);
        }

        String email = request.email() == null || request.email().isBlank()
                ? null : request.email().trim();
        Student student = new Student(code, request.studentName().trim(), email);

        try {
            Student saved = studentRepository.saveAndFlush(student);
            log.info("STUDENT_CREATED studentId={} studentCode={}", saved.getId(), saved.getStudentCode());
            return StudentMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            // La restricción UNIQUE también cubre dos altas concurrentes con el mismo código.
            throw new DuplicateStudentCodeException(code);
        }
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return studentRepository.findAllByOrderByIdAsc().stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return StudentMapper.toResponse(requireStudent(id));
    }

    @Transactional(readOnly = true)
    public StudentResponse findByCode(String code) {
        String normalizedCode = normalizeCode(code);
        Student student = studentRepository.findByStudentCodeIgnoreCase(normalizedCode)
                .orElseThrow(() -> notFound(normalizedCode));
        return StudentMapper.toResponse(student);
    }

    @Transactional
    public StudentResponse changeStatus(Long id, StudentStatus status) {
        Student student = requireStudent(id);
        student.setStatus(status);
        Student saved = studentRepository.save(student);
        log.info("STUDENT_STATUS_CHANGED studentId={} status={}", saved.getId(), saved.getStatus());
        return StudentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StudentAcademicHistoryResponse academicHistory(Long id) {
        Student student = requireStudent(id);
        List<Grade> grades = gradeRepository.findByStudent_IdOrderByRegisteredAtDescIdDesc(id);
        return StudentMapper.toAcademicHistory(student, grades);
    }

    /**
     * Punto de validación para A4: no se debe crear una inscripción con un estudiante inactivo.
     */
    @Transactional(readOnly = true)
    public Student requireActiveForEnrollment(Long id) {
        Student student = requireStudent(id);
        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new InactiveStudentException(id);
        }
        return student;
    }

    private Student requireStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    private StudentNotFoundException notFound(Object identifier) {
        log.warn("STUDENT_NOT_FOUND identifier={}", identifier);
        return new StudentNotFoundException(identifier);
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}