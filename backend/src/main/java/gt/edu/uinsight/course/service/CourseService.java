package gt.edu.uinsight.course.service;

import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.course.dto.request.CreateCourseRequest;
import gt.edu.uinsight.course.dto.request.UpdateCourseRequest;
import gt.edu.uinsight.course.dto.response.CourseResponse;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    CourseResponse create(CreateCourseRequest request);

    PageResponse<CourseResponse> findAll(String status, Pageable pageable);

    CourseResponse findById(Long id);

    CourseResponse findByCode(String code);

    CourseResponse update(Long id, UpdateCourseRequest request);

    CourseResponse changeStatus(Long id, ChangeStatusRequest request);
}
