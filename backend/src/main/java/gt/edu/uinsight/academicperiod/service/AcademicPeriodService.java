package gt.edu.uinsight.academicperiod.service;

import gt.edu.uinsight.academicperiod.dto.request.CreateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import gt.edu.uinsight.academicperiod.entity.PeriodStatus;
import gt.edu.uinsight.academicperiod.mapper.AcademicPeriodMapper;
import gt.edu.uinsight.academicperiod.repository.AcademicPeriodRepository;
import gt.edu.uinsight.exception.BusinessRuleException;
import gt.edu.uinsight.exception.DuplicateResourceException;
import gt.edu.uinsight.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AcademicPeriodService {

    private static final Logger log = LoggerFactory.getLogger(AcademicPeriodService.class);

    private final AcademicPeriodRepository repository;
    private final AcademicPeriodMapper mapper;

    public AcademicPeriodService(AcademicPeriodRepository repository, AcademicPeriodMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public AcademicPeriodResponse create(CreateAcademicPeriodRequest request) {
        if (!request.startDate().isBefore(request.endDate())) {
            throw new BusinessRuleException("La fecha de inicio debe ser anterior a la fecha de fin (RN-02)");
        }

        repository.findByNameIgnoreCaseAndYear(request.name(), request.year())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Ya existe un periodo academico con el nombre '" + request.name()
                                    + "' en el anio " + request.year() + " (RN-07)");
                });

        AcademicPeriod period = new AcademicPeriod(
                request.name(),
                request.year(),
                request.startDate(),
                request.endDate(),
                PeriodStatus.PLANNED
        );

        AcademicPeriod saved = repository.save(period);
        log.info("ACADEMIC_PERIOD_CREATED id={} name={} year={}", saved.getId(), saved.getName(), saved.getYear());
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriodResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AcademicPeriodResponse findById(Long id) {
        AcademicPeriod period = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un periodo academico con id " + id));
        return mapper.toResponse(period);
    }
}
