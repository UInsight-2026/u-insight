package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.request.CreateCheckRequest;
import gt.edu.uinsight.system.dto.response.CheckResponse;
import gt.edu.uinsight.system.entity.SystemCheckLog;
import gt.edu.uinsight.system.exception.CheckNotFoundException;
import gt.edu.uinsight.system.repository.SystemCheckLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemCheckService {

    private final SystemCheckLogRepository repository;

    public SystemCheckService(SystemCheckLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CheckResponse createCheck(CreateCheckRequest request) {
        SystemCheckLog log = new SystemCheckLog();
        log.setComponent(request.component());
        log.setStatus(request.status());
        log.setMessage(request.message());

        return toResponse(repository.save(log));
    }

    @Transactional(readOnly = true)
    public List<CheckResponse> getAllChecks() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CheckResponse getCheckById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CheckNotFoundException(id));
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
