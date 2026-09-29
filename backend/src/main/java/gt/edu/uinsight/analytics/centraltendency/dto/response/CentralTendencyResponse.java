package gt.edu.uinsight.analytics.centraltendency.dto.response;
//librerías de Java


import java.util.List;

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
public record CentralTendencyResponse(
        int sampleSize,
        Double mean,
        Double median,
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

