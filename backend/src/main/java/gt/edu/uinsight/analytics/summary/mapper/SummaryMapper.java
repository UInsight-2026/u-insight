package gt.edu.uinsight.analytics.summary.mapper;


import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.summary.dto.response.SectionSummaryResponse;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Convierte los resultados individuales de B1-B5 en el objeto consolidado
 * SectionSummaryResponse que expone B6.
 *
 * Cada componente llega como Optional<T>: vacío significa que el servicio
 * correspondiente falló o no respondió a tiempo (regla de negocio #4 del
 * documento), y por lo tanto debe agregarse a unavailableComponents en vez
 * de provocar un error total.
 *
 * NOTA: ajustar los tipos CentralTendencyResponse / PositionResponse /
 * DispersionResponse / TrendResponse si Joshua, Ximena o Diego terminan
 * usando nombres distintos para las DTOs de B1-B4 o para la respuesta final.
 */
@Component
public class SummaryMapper {

    private static final String CENTRAL_TENDENCY = "centralTendency";
    private static final String POSITION = "position";
    private static final String DISPERSION = "dispersion";
    private static final String TREND = "trend";

    /**
     * Construye la respuesta consolidada de la sección.
     *
     * @param sectionId        id de la sección consultada
     * @param centralTendency  resultado de B1, vacío si falló/timeout
     * @param position         resultado de B2, vacío si falló/timeout
     * @param dispersion       resultado de B3, vacío si falló/timeout
     * @param trend            resultado de B4, vacío si falló/timeout
     * @param studentsAtRisk   conteo de estudiantes en riesgo (de B5 / riskevaluation)
     * @return SectionSummaryResponse listo para devolver en el controller
     */
    public SectionSummaryResponse toSectionSummaryResponse(
            Long sectionId,
            Optional<CentralTendencyResponse> centralTendency,
            Optional<SectionPositionResponse> position,
            Optional<DispersionResponse> dispersion,
            Optional<TrendResponse> trend,
            Integer studentsAtRisk
    ) {
        List<String> unavailableComponents = new ArrayList<>();

        if (centralTendency.isEmpty()) {
            unavailableComponents.add(CENTRAL_TENDENCY);
        }
        if (position.isEmpty()) {
            unavailableComponents.add(POSITION);
        }
        if (dispersion.isEmpty()) {
            unavailableComponents.add(DISPERSION);
        }
        if (trend.isEmpty()) {
            unavailableComponents.add(TREND);
        }

        SectionSummaryResponse response = new SectionSummaryResponse();
        response.setSectionId(sectionId);
        response.setCentralTendency(centralTendency.orElse(null));
        response.setPosition(position.orElse(null));
        response.setDispersion(dispersion.orElse(null));
        response.setTrend(trend.orElse(null));
        response.setStudentsAtRisk(studentsAtRisk);
        response.setUnavailableComponents(unavailableComponents);

        return response;
    }
}
