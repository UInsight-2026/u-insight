package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.request.CreateCheckRequest;
import gt.edu.uinsight.system.dto.request.UpdateCheckStatusRequest;
import gt.edu.uinsight.system.dto.response.CheckResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.entity.SystemCheckLog;
import gt.edu.uinsight.system.exception.CheckNotFoundException;
import gt.edu.uinsight.system.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.system.logging.SystemEventLogger;
import gt.edu.uinsight.system.repository.SystemCheckLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemCheckService {

    private final SystemCheckLogRepository repository;
    private final SystemEventLogger eventLogger;

    public SystemCheckService(SystemCheckLogRepository repository, SystemEventLogger eventLogger) {
        this.repository = repository;
        this.eventLogger = eventLogger;
    }

    @Transactional
    public CheckResponse createCheck(CreateCheckRequest request) {
        SystemCheckLog log = new SystemCheckLog();
        log.setComponent(request.component());
        log.setStatus(request.status());
        log.setMessage(request.message());

        SystemCheckLog saved = repository.save(log);

        eventLogger.info("RESOURCE_CREATED", 201, "System check created with id " + saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CheckResponse> getAllChecks() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CheckResponse> getChecks(String component, CheckStatus status, Pageable pageable) {
        String componentFilter = (component == null || component.isBlank()) ? null : component.trim();

        return repository.findByFilters(componentFilter, status, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CheckResponse getCheckById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CheckNotFoundException(id));
    }

    @Transactional
    public CheckResponse updateStatus(Long id, UpdateCheckStatusRequest request) {
        SystemCheckLog log = repository.findById(id)
                .orElseThrow(() -> new CheckNotFoundException(id));

        CheckStatus currentStatus = log.getStatus();
        CheckStatus newStatus = request.status();

        if (currentStatus == CheckStatus.DOWN && newStatus == CheckStatus.UP) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        log.setStatus(newStatus);

        return toResponse(repository.save(log));
    }

    private CheckResponse toResponse(SystemCheckLog log) {
        return new CheckResponse(
                log.getId(),
                log.getComponent(),
                log.getStatus(),
                log.getMessage(),
                log.getCheckedAt()
        );
    }
}
