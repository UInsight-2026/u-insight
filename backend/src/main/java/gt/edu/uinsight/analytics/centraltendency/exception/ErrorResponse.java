package gt.edu.uinsight.analytics.centraltendency.exception;

import io.swagger.v3.oas.annotations.media.Schema; 

@Schema(description = "Estructura estandarizada para el manejo de excepciones de la API")
public record ErrorResponse(
    
    @Schema(description = "Código de estado HTTP", example = "400")
    int status,
    
    @Schema(description = "Mensaje detallado del error de validación o negocio", example = "El ID de la sección debe ser mayor que cero")
    String message,
    
    @Schema(description = "Marca de tiempo del servidor", example = "1727813449000")
    long timestamp
    
) {}