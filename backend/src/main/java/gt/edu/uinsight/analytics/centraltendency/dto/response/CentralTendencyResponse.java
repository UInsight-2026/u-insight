package gt.edu.uinsight.analytics.centraltendency.dto.response;

//librerías de Java
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema; // <-- Agrega este import

/**
 * Result of the central-tendency calculations for a sample.
 *
 * <p>The component names are the JSON contract: {@code sampleSize},
 * {@code mean}, {@code median}, and {@code mode}.</p>
 *
 * @param sampleSize number of values processed
 * @param mean arithmetic mean; {@code null} when the sample is empty
 * @param median median; {@code null} when the sample is empty
 * @param mode modes of the sample; empty when no representative mode exists
 */
@Schema(description = "Objeto que contiene los resultados del cálculo de tendencia central")
public record CentralTendencyResponse(
    
    @Schema(description = "Cantidad total de notas procesadas", example = "5")
    int sampleSize,
    
    @Schema(description = "Promedio aritmético de las notas", example = "73.6")
    Double mean,
    
    @Schema(description = "Valor central de la distribución", example = "73.0")
    Double median,
    
    @Schema(description = "Valores que más se repiten (puede ser bimodal/multimodal)", example = "[70.0]")
    List<Double> mode
    
) {
    public CentralTendencyResponse {
        mode = mode == null ? List.of() : List.copyOf(mode);
    }

    /** Creates the response for an empty sample. */
    public static CentralTendencyResponse empty() {
        return new CentralTendencyResponse(0, null, null, List.of());
    }
}
