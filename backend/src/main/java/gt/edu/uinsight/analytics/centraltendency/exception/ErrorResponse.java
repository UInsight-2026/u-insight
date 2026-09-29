package gt.edu.uinsight.analytics.centraltendency.exception;

public record ErrorResponse(int status, String message, long timestamp) {}