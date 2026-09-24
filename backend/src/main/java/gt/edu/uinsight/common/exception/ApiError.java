package gt.edu.uinsight.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

/** Cuerpo estandar de respuesta para los errores del API. */
public class ApiError {

    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String message;
    private Map<String, String> fields;

    public ApiError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ApiError(int status, String error, String message, Map<String, String> fields) {
        this(status, error, message);
        this.fields = fields;
    }

    public LocalDateTime getTimestamp() { return timestamp; }

    public int getStatus() { return status; }

    public String getError() { return error; }

    public String getMessage() { return message; }

    public Map<String, String> getFields() { return fields; }
}
