package gt.edu.uinsight.alert.service;

import gt.edu.uinsight.alert.dto.AlertResponse;
import gt.edu.uinsight.alert.dto.CreateAlertRequest;
import gt.edu.uinsight.alert.dto.UpdateAlertStatusRequest;
import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertResponse createAlert(CreateAlertRequest request) {
        logger.info("Integración: Creando nueva alerta generada por otra célula - {}", request.getTitle());
        Alert alert = new Alert();
        alert.setSectionId(request.getSectionId());
        alert.setAlertType(request.getAlertType());
        alert.setSeverity(request.getSeverity());
        alert.setTitle(request.getTitle());
        alert.setDescription(request.getDescription());
        alert.setStatus("NEW");
        alert.setGeneratedAt(LocalDateTime.now());
        
        return mapToResponse(alertRepository.save(alert));
    }

    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AlertResponse> getActiveAlerts() {
        return alertRepository.findActiveAlerts().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public AlertResponse getAlertById(Long id) {
        return mapToResponse(alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada con ID: " + id)));
    }

    public AlertResponse updateAlertStatus(Long id, UpdateAlertStatusRequest request) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada con ID: " + id));

        String currentStatus = alert.getStatus();
        String newStatus = request.getStatus();

        if ("RESOLVED".equals(currentStatus) || "DISMISSED".equals(currentStatus)) {
            throw new IllegalArgumentException("RN05: No se puede cambiar el estado de una alerta ya cerrada.");
        }

        if ("NEW".equals(currentStatus) && "RESOLVED".equals(newStatus)) {
            throw new IllegalArgumentException("RN01: Transición inválida. Una alerta NEW debe pasar a revisión antes de resolverse.");
        }

        alert.setStatus(newStatus);
        if ("RESOLVED".equals(newStatus) || "DISMISSED".equals(newStatus)) {
            alert.setResolvedAt(LocalDateTime.now());
        }

        return mapToResponse(alertRepository.save(alert));
    }

    private AlertResponse mapToResponse(Alert alert) {
        AlertResponse response = new AlertResponse();
        response.setId(alert.getId());
        response.setSectionId(alert.getSectionId());
        response.setAlertType(alert.getAlertType());
        response.setSeverity(alert.getSeverity());
        response.setTitle(alert.getTitle());
        response.setDescription(alert.getDescription());
        response.setStatus(alert.getStatus());
        response.setGeneratedAt(alert.getGeneratedAt());
        response.setResolvedAt(alert.getResolvedAt());
        return response;
    }
}