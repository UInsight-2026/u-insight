package gt.edu.uinsight.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateAlertRequest {
    
    @NotNull(message = "El ID de la sección es obligatorio")
    private Long sectionId;

    @NotBlank(message = "El tipo de alerta no puede estar vacío")
    private String alertType;

    @NotBlank(message = "La severidad es obligatoria")
    private String severity;

    @NotBlank(message = "El título no puede estar vacío")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    // Getters
    public Long getSectionId() { return sectionId; }
    public String getAlertType() { return alertType; }
    public String getSeverity() { return severity; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    // Setters
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public void setSeverity(String severity) { this.severity = severity; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}