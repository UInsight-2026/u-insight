package gt.edu.uinsight.section.service;

import gt.edu.uinsight.common.exception.BusinessRuleException;
import gt.edu.uinsight.common.exception.DuplicateResourceException;
import gt.edu.uinsight.common.exception.ResourceNotFoundException;
import gt.edu.uinsight.section.dto.SectionRequest;
import gt.edu.uinsight.section.dto.SectionResponse;
import gt.edu.uinsight.section.model.Section;
import gt.edu.uinsight.section.repository.SectionRepository;
import gt.edu.uinsight.teacher.model.Teacher;
import gt.edu.uinsight.teacher.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio de secciones:
 * - El codigo de la seccion es unico.
 * - No se puede asignar un docente inactivo a una nueva seccion.
 */
@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final TeacherRepository teacherRepository;

    public SectionService(SectionRepository sectionRepository, TeacherRepository teacherRepository) {
        this.sectionRepository = sectionRepository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> findAll() {
        return sectionRepository.findAll().stream().map(SectionResponse::from).toList();
    }

    @Transactional
    public SectionResponse create(SectionRequest request) {
        String sectionCode = request.getSectionCode().trim().toUpperCase();
        if (sectionRepository.existsBySectionCodeIgnoreCase(sectionCode)) {
            throw new DuplicateResourceException("Ya existe una seccion con el codigo " + sectionCode);
        }

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el docente con id " + request.getTeacherId()));

        if (!teacher.isActive()) {
            throw new BusinessRuleException("El docente " + teacher.getTeacherCode()
                    + " esta INACTIVE y no puede asignarse a nuevas secciones");
        }

        Section section = new Section(
                sectionCode,
                request.getCourseName().trim(),
                request.getAcademicTerm().trim(),
                teacher);

        return SectionResponse.from(sectionRepository.save(section));
    }
}
