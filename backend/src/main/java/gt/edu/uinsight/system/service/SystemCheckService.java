package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.entity.SystemCheckLog;
import gt.edu.uinsight.system.repository.SystemCheckLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SystemCheckService {

    private final SystemCheckLogRepository repository;

    public SystemCheckService(SystemCheckLogRepository repository) {
        this.repository = repository;
    }

    public SystemCheckLog saveCheck(SystemCheckLog log) {
        return repository.save(log);
    }

    public List<SystemCheckLog> getAllChecks() {
        return repository.findAll();
    }

    public Optional<SystemCheckLog> getCheckById(Long id) {
        return repository.findById(id);
    }
}