package gt.edu.uinsight.course.service;

import gt.edu.uinsight.course.dto.request.CreateCourseRequest;
import gt.edu.uinsight.course.dto.response.CourseResponse;
import gt.edu.uinsight.course.entity.Course;
import gt.edu.uinsight.course.entity.CourseStatus;
import gt.edu.uinsight.course.mapper.CourseMapper;
import gt.edu.uinsight.course.repository.CourseRepository;
import gt.edu.uinsight.exception.BusinessRuleException;
import gt.edu.uinsight.exception.DuplicateResourceException;
import gt.edu.uinsight.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository repository;
    private final CourseMapper mapper;

    public CourseService(CourseRepository repository, CourseMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public CourseResponse create(CreateCourseRequest request) {
        if (request.credits() != null && request.credits() <= 0) {
            throw new BusinessRuleException("Los creditos deben ser mayores que cero (RN-08)");
        }

        repository.findByCodeIgnoreCase(request.code())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Ya existe un curso con el codigo '" + request.code() + "' (RN-01)");
                });

        Course course = new Course(
                request.code(),
                request.name(),
                request.description(),
                request.credits(),
                CourseStatus.ACTIVE
        );

        Course saved = repository.save(course);
        log.info("COURSE_CREATED id={} code={}", saved.getId(), saved.getCode());
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un curso con id " + id));
        return mapper.toResponse(course);
    }
}
