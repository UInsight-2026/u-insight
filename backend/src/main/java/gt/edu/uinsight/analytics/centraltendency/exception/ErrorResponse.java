package gt.edu.uinsight.analytics.centraltendency.exception;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Formato estándar U-Insight para errores de la API:
 * {@code { timestamp, status, error, message, details, traceId }}
 * (mismo contrato usado por A5, B4 y C4).
 */
@Schema(description = "Estructura estandarizada para el manejo de excepciones de la API (formato estándar U-Insight)")
public record ErrorResponse(

    @Schema(description = "Momento en el que ocurrió el error")
    LocalDateTime timestamp,

    @Schema(description = "Código de estado HTTP", example = "400")
    int status,

    @Schema(description = "Código corto del tipo de error", example = "VALIDATION_ERROR")
    String error,

    @Schema(description = "Mensaje detallado del error de validación o negocio", example = "El ID de la sección debe ser mayor que cero")
    String message,

    @Schema(description = "Detalles adicionales del error, si aplica")
    List<String> details,

    @Schema(description = "Identificador de trazabilidad de la petición", example = "b1-7f2e1c3a")
    String traceId

) {}
