package gt.edu.uinsight.teacher.service;

import gt.edu.uinsight.common.exception.BusinessRuleException;
import gt.edu.uinsight.common.exception.DuplicateResourceException;
import gt.edu.uinsight.common.exception.ResourceNotFoundException;
import gt.edu.uinsight.section.dto.SectionResponse;
import gt.edu.uinsight.section.repository.SectionRepository;
import gt.edu.uinsight.teacher.dto.TeacherRequest;
import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.model.TeacherStatus;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio del modulo de docentes:
 * - El codigo del docente es unico.
 * - El correo, cuando se utiliza, debe tener formato valido.
 * - Un docente con historial de secciones no se elimina fisicamente, solo se inactiva.
 */
@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SectionRepository sectionRepository;

    public TeacherService(TeacherRepository teacherRepository, SectionRepository sectionRepository) {
        this.teacherRepository = teacherRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el docente con id " + id));
    }

    @Transactional
    public Teacher create(TeacherRequest request) {
        String teacherCode = normalizeCode(request.getTeacherCode());
        if (teacherRepository.existsByTeacherCodeIgnoreCase(teacherCode)) {
            throw new DuplicateResourceException("Ya existe un docente con el codigo " + teacherCode);
        }

        Teacher teacher = new Teacher(
                teacherCode,
                request.getTeacherName().trim(),
                normalizeEmail(request.getEmail()),
                request.getStatus() != null ? request.getStatus() : TeacherStatus.ACTIVE);

        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher update(Long id, TeacherRequest request) {
        Teacher teacher = findById(id);
        String teacherCode = normalizeCode(request.getTeacherCode());

        if (teacherRepository.existsByTeacherCodeIgnoreCaseAndIdNot(teacherCode, id)) {
            throw new DuplicateResourceException("Ya existe otro docente con el codigo " + teacherCode);
        }

        teacher.setTeacherCode(teacherCode);
        teacher.setTeacherName(request.getTeacherName().trim());
        teacher.setEmail(normalizeEmail(request.getEmail()));
        if (request.getStatus() != null) {
            teacher.setStatus(request.getStatus());
        }

        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher changeStatus(Long id, TeacherStatus status) {
        Teacher teacher = findById(id);
        teacher.setStatus(status);
        return teacherRepository.save(teacher);
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> findSectionsByTeacher(Long id) {
        Teacher teacher = findById(id);
        return sectionRepository
                .findByTeacherIdOrderByAcademicTermDescSectionCodeAsc(teacher.getId())
                .stream()
                .map(SectionResponse::from)
                .toList();
    }

    /**
     * Elimina fisicamente al docente solo si no tiene historial academico.
     * Si ya tiene secciones asignadas la baja debe ser logica (PATCH /status).
     */
    @Transactional
    public void delete(Long id) {
        Teacher teacher = findById(id);
        if (sectionRepository.existsByTeacherId(teacher.getId())) {
            throw new BusinessRuleException("El docente tiene secciones asignadas y no puede eliminarse. "
                    + "Utilice PATCH /api/v1/teachers/" + id + "/status para darlo de baja como INACTIVE");
        }
        teacherRepository.delete(teacher);
    }

    private String normalizeCode(String teacherCode) {
        return teacherCode.trim().toUpperCase();
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase();
    }
}
