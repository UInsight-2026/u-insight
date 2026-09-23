package gt.edu.uinsight.system.service;

import gt.edu.uinsight.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.system.dto.UpdateCheckStatusRequest;
import gt.edu.uinsight.system.model.SystemCheck;
import gt.edu.uinsight.system.repository.SystemCheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Service
public class SystemCheckService {

    private final SystemCheckRepository repository;

    public SystemCheckService(SystemCheckRepository repository) {
        this.repository = repository;
    }
    public Page<SystemCheck> findAll(String status, String component, Pageable pageable) {
    String statusFilter = (status == null) ? "" : status;
    String componentFilter = (component == null) ? "" : component;

    return repository.findByStatusContainingIgnoreCaseAndComponentContainingIgnoreCase(
            statusFilter, componentFilter, pageable
    );
}

    public SystemCheck updateStatus(Long id, UpdateCheckStatusRequest request) {
        SystemCheck check = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobación no encontrada con ID: " + id));

        String oldStatus = check.getStatus();
        String newStatus = request.getStatus().toUpperCase();

        // Regla de negocio: DOWN -> UP directamente está prohibido
        if ("DOWN".equalsIgnoreCase(oldStatus) && "UP".equalsIgnoreCase(newStatus)) {
            throw new InvalidStatusTransitionException("Una comprobación en estado DOWN no puede pasar directamente a UP sin pasar por DEGRADED.");
        }

        check.setStatus(newStatus);
        return repository.save(check);
    }
}