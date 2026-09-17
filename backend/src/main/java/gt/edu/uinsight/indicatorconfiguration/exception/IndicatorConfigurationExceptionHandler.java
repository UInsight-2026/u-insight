package gt.edu.uinsight.indicatorconfiguration.exception;

import gt.edu.uinsight.indicatorconfiguration.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "gt.edu.uinsight.indicatorconfiguration")
public class IndicatorConfigurationExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorConfigurationExceptionHandler.class);

    @ExceptionHandler(IndicatorConfigurationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            IndicatorConfigurationNotFoundException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.NOT_FOUND, "INDICATOR_CONFIGURATION_NOT_FOUND", ex.getMessage(), request, null);
    }

    @ExceptionHandler(IndicatorConfigurationConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            IndicatorConfigurationConflictException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.CONFLICT, "INDICATOR_CONFIGURATION_CONFLICT", ex.getMessage(), request, null);
    }

    @ExceptionHandler(IndicatorConfigurationBadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            IndicatorConfigurationBadRequestException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "La solicitud contiene datos invalidos",
                request,
                fieldErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", "El cuerpo JSON no es valido", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        LOGGER.error("Error no controlado en C1", ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Ocurrio un error interno en el servidor",
                request,
                null
        );
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                request.getRequestURI(),
                validationErrors
        );
        return ResponseEntity.status(status).body(response);
    }
}
