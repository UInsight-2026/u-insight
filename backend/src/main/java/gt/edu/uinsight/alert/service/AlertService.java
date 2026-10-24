package gt.edu.uinsight.alert.service;

import gt.edu.uinsight.alert.dto.request.UpdateAlertStatusRequest;
import gt.edu.uinsight.alert.dto.response.AlertResponse;
import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.model.AlertStatus;
import gt.edu.uinsight.alert.repository.AlertRepository;
import gt.edu.uinsight.common.exception.BusinessRuleException;
import gt.edu.uinsight.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Celula C3 - Ciclo de vida de las alertas.
 *
 * Administra una alerta despues de que la celula B7 la genera: consulta,
 * listado de activas y transicion de estado (RN: sin saltos ni retrocesos
 * arbitrarios entre estados).
 */
@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private static final Set<AlertStatus> INACTIVE_STATUSES =
            EnumSet.of(AlertStatus.RESOLVED, AlertStatus.DISMISSED);

    private static final Map<AlertStatus, Set<AlertStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(AlertStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(AlertStatus.NEW, EnumSet.of(AlertStatus.UNDER_REVIEW, AlertStatus.DISMISSED));
        ALLOWED_TRANSITIONS.put(AlertStatus.UNDER_REVIEW, EnumSet.of(AlertStatus.IN_PROGRESS, AlertStatus.DISMISSED));
        ALLOWED_TRANSITIONS.put(AlertStatus.IN_PROGRESS, EnumSet.of(AlertStatus.RESOLVED, AlertStatus.DISMISSED));
        ALLOWED_TRANSITIONS.put(AlertStatus.RESOLVED, EnumSet.noneOf(AlertStatus.class));
        ALLOWED_TRANSITIONS.put(AlertStatus.DISMISSED, EnumSet.noneOf(AlertStatus.class));
    }

    private final AlertRepository repository;

    public AlertService(AlertRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> findActive() {
        return repository.findByStatusNotIn(INACTIVE_STATUSES).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AlertResponse findById(Long id) {
        return toResponse(getExisting(id));
    }

    @Transactional
    public AlertResponse updateStatus(Long id, UpdateAlertStatusRequest request) {
        Alert alert = getExisting(id);
        AlertStatus current = alert.getStatus();
        AlertStatus next = request.status();

        if (!ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(next)) {
            log.warn("ALERT_STATUS_TRANSITION_REJECTED alertId={} from={} to={}", id, current, next);
            throw new BusinessRuleException(
                    "No se permite la transición de " + current + " a " + next);
        }

        alert.setStatus(next);
        if (next == AlertStatus.RESOLVED || next == AlertStatus.DISMISSED) {
            alert.setResolvedAt(LocalDateTime.now());
        }

        Alert saved = repository.save(alert);
        log.info("ALERT_STATUS_CHANGED alertId={} from={} to={}", id, current, next);

        return toResponse(saved);
    }

    private Alert getExisting(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("ALERT_NOT_FOUND alertId={}", id);
                    return new ResourceNotFoundException("No existe una alerta con id " + id);
                });
    }

    private AlertResponse toResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getSectionId(),
                alert.getAlertType(),
                alert.getSeverity(),
                alert.getTitle(),
                alert.getDescription(),
                alert.getStatus(),
                alert.getGeneratedAt(),
                alert.getResolvedAt()
        );
    }
}
