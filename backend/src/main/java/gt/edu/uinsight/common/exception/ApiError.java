package gt.edu.uinsight.common.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    private final String timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final List<String> details;
    private final String traceId;

    public ApiError(int status, String error, String message, List<String> details, String traceId) {
        this.timestamp = LocalDateTime.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.traceId = traceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

    public String getTraceId() {
        return traceId;
    }
}
