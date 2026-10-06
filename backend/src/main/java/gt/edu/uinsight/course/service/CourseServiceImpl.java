package gt.edu.uinsight.course.service;

import gt.edu.uinsight.academicperiod.support.StatusParser;
import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.academicperiod.support.exception.AcademicBusinessRuleException;
import gt.edu.uinsight.academicperiod.support.exception.AcademicResourceNotFoundException;
import gt.edu.uinsight.academicperiod.support.logging.AcademicEventLogger;
import gt.edu.uinsight.course.dto.request.CreateCourseRequest;
import gt.edu.uinsight.course.dto.request.UpdateCourseRequest;
import gt.edu.uinsight.course.dto.response.CourseResponse;
import gt.edu.uinsight.course.entity.Course;
import gt.edu.uinsight.course.entity.CourseStatus;
import gt.edu.uinsight.course.mapper.CourseMapper;
import gt.edu.uinsight.course.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio de cursos: RN-01 codigo unico sin distinguir mayusculas,
 * RN-05 baja logica por estado, RN-08 creditos mayores que cero y RN-09 estado
 * expuesto en la respuesta.
 */
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;
    private final CourseMapper mapper;
    private final AcademicEventLogger eventLogger;

    public CourseServiceImpl(CourseRepository repository, CourseMapper mapper, AcademicEventLogger eventLogger) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventLogger = eventLogger;
    }

    @Override
    @Transactional
    public CourseResponse create(CreateCourseRequest request) {
        validateCredits(request.credits());
        String code = request.code().trim();
        if (repository.existsByCodeIgnoreCase(code)) {
            throw AcademicBusinessRuleException.duplicate("RN-01",
                    "Ya existe un curso con el codigo '" + code + "'");
        }

        Course saved = repository.save(mapper.toEntity(request));
        eventLogger.info("COURSE_CREATED", 201,
                "Course created successfully: id=" + saved.getId() + " code=" + saved.getCode());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> findAll(String status, Pageable pageable) {
        CourseStatus filter = StatusParser.parseOptional(CourseStatus.class, status);
        Page<Course> page = filter == null
                ? repository.findAll(pageable)
                : repository.findByStatus(filter, pageable);
        return PageResponse.from(page, mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse findByCode(String code) {
        return repository.findByCodeIgnoreCase(code.trim())
                .map(mapper::toResponse)
                .orElseThrow(() -> new AcademicResourceNotFoundException(
                        "No existe un curso con el codigo '" + code + "'"));
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, UpdateCourseRequest request) {
        Course course = getOrThrow(id);
        validateCredits(request.credits());

        course.updateDetails(request.name().trim(), request.description(), request.credits());
        Course saved = repository.save(course);
        eventLogger.info("COURSE_UPDATED", 200, "Course updated successfully: id=" + id);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CourseResponse changeStatus(Long id, ChangeStatusRequest request) {
        CourseStatus target = StatusParser.parse(CourseStatus.class, request.status());
        Course course = getOrThrow(id);
        CourseStatus current = course.getStatus();

        if (!current.canTransitionTo(target)) {
            throw AcademicBusinessRuleException.conflict("RN-06",
                    "El curso " + id + " ya esta en estado " + current);
        }

        course.changeStatus(target);
        Course saved = repository.save(course);
        eventLogger.info("COURSE_STATUS_CHANGED", 200,
                "Course status changed: id=" + id + " " + current + " -> " + target);
        return mapper.toResponse(saved);
    }

    private Course getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AcademicResourceNotFoundException("No existe un curso con id " + id));
    }

    private void validateCredits(Integer credits) {
        if (credits != null && credits <= 0) {
            throw AcademicBusinessRuleException.badRequest("RN-08",
                    "Si se envian creditos, deben ser mayores que cero");
        }
    }
}
