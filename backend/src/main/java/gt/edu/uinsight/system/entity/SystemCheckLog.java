package gt.edu.uinsight.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_check_log")
public class SystemCheckLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String component;
    private String status;
    private String message;
    private LocalDateTime checkedAt = LocalDateTime.now();

    // Genera los Getters y Setters (o usa @Data si tienen Lombok configurado)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
}