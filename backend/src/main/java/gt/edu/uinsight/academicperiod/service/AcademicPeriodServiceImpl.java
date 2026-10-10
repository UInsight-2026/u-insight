package gt.edu.uinsight.academicperiod.service;

import gt.edu.uinsight.academicperiod.dto.request.CreateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.request.UpdateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import gt.edu.uinsight.academicperiod.entity.PeriodStatus;
import gt.edu.uinsight.academicperiod.mapper.AcademicPeriodMapper;
import gt.edu.uinsight.academicperiod.repository.AcademicPeriodRepository;
import gt.edu.uinsight.academicperiod.support.StatusParser;
import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.academicperiod.support.exception.AcademicBusinessRuleException;
import gt.edu.uinsight.academicperiod.support.exception.AcademicResourceNotFoundException;
import gt.edu.uinsight.academicperiod.support.logging.AcademicEventLogger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Reglas de negocio de periodos academicos:
 * RN-02 fechas, RN-03 un solo ACTIVE, RN-04 CLOSED no se modifica,
 * RN-05 sin borrado fisico, RN-06 transiciones (en {@link PeriodStatus}),
 * RN-07 nombre unico por anio.
 */
@Service
public class AcademicPeriodServiceImpl implements AcademicPeriodService {

    private final AcademicPeriodRepository repository;
    private final AcademicPeriodMapper mapper;
    private final AcademicEventLogger eventLogger;

    public AcademicPeriodServiceImpl(AcademicPeriodRepository repository,
                                     AcademicPeriodMapper mapper,
                                     AcademicEventLogger eventLogger) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventLogger = eventLogger;
    }

    @Override
    @Transactional
    public AcademicPeriodResponse create(CreateAcademicPeriodRequest request) {
        validateDates(request.startDate(), request.endDate());
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCaseAndYear(name, request.year())) {
            throw duplicateName(name, request.year());
        }

        AcademicPeriod saved = repository.save(mapper.toEntity(request));
        eventLogger.info("ACADEMIC_PERIOD_CREATED", 201,
                "Academic period created successfully: id=" + saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AcademicPeriodResponse> findAll(String status, Pageable pageable) {
        PeriodStatus filter = StatusParser.parseOptional(PeriodStatus.class, status);
        Page<AcademicPeriod> page = filter == null
                ? repository.findAll(pageable)
                : repository.findByStatus(filter, pageable);
        return PageResponse.from(page, mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicPeriodResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicPeriodResponse findActive() {
        return repository.findFirstByStatus(PeriodStatus.ACTIVE)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AcademicResourceNotFoundException(
                        "No hay un periodo academico en estado ACTIVE"));
    }

    @Override
    @Transactional
    public AcademicPeriodResponse update(Long id, UpdateAcademicPeriodRequest request) {
        AcademicPeriod period = getOrThrow(id);
        if (period.getStatus() == PeriodStatus.CLOSED) {
            throw AcademicBusinessRuleException.conflict("RN-04",
                    "El periodo academico " + id + " esta CLOSED y no acepta modificaciones");
        }
        validateDates(request.startDate(), request.endDate());
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCaseAndYearAndIdNot(name, period.getYear(), id)) {
            throw duplicateName(name, period.getYear());
        }

        period.updateDetails(name, request.startDate(), request.endDate());
        AcademicPeriod saved = repository.save(period);
        eventLogger.info("ACADEMIC_PERIOD_UPDATED", 200,
                "Academic period updated successfully: id=" + id);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AcademicPeriodResponse changeStatus(Long id, ChangeStatusRequest request) {
        PeriodStatus target = StatusParser.parse(PeriodStatus.class, request.status());
        AcademicPeriod period = getOrThrow(id);
        PeriodStatus current = period.getStatus();

        if (!current.canTransitionTo(target)) {
            String rule = current == PeriodStatus.CLOSED ? "RN-04" : "RN-06";
            throw AcademicBusinessRuleException.conflict(rule,
                    "Transicion de estado invalida: " + current + " -> " + target
                            + ". Solo se permite PLANNED -> ACTIVE y ACTIVE -> CLOSED");
        }
        if (target == PeriodStatus.ACTIVE && repository.existsByStatus(PeriodStatus.ACTIVE)) {
            throw AcademicBusinessRuleException.conflict("RN-03",
                    "Ya existe un periodo academico en estado ACTIVE; cierrelo antes de activar otro");
        }

        period.changeStatus(target);
        AcademicPeriod saved = repository.save(period);
        String operation = target == PeriodStatus.ACTIVE ? "ACADEMIC_PERIOD_ACTIVATED" : "ACADEMIC_PERIOD_CLOSED";
        eventLogger.info(operation, 200,
                "Academic period status changed: id=" + id + " " + current + " -> " + target);
        return mapper.toResponse(saved);
    }

    private AcademicPeriod getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AcademicResourceNotFoundException(
                        "No existe un periodo academico con id " + id));
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (!startDate.isBefore(endDate)) {
            throw AcademicBusinessRuleException.badRequest("RN-02",
                    "La fecha de inicio debe ser estrictamente anterior a la fecha de fin");
        }
    }

    private AcademicBusinessRuleException duplicateName(String name, Integer year) {
        return AcademicBusinessRuleException.duplicate("RN-07",
                "Ya existe un periodo academico con el nombre '" + name + "' en el anio " + year);
    }
}
