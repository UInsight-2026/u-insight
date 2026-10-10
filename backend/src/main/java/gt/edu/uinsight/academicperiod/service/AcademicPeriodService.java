package gt.edu.uinsight.academicperiod.service;

import gt.edu.uinsight.academicperiod.dto.request.CreateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.request.UpdateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AcademicPeriodService {

    AcademicPeriodResponse create(CreateAcademicPeriodRequest request);

    PageResponse<AcademicPeriodResponse> findAll(String status, Pageable pageable);

    AcademicPeriodResponse findById(Long id);

    AcademicPeriodResponse findActive();

    AcademicPeriodResponse update(Long id, UpdateAcademicPeriodRequest request);

    AcademicPeriodResponse changeStatus(Long id, ChangeStatusRequest request);
}
