package gt.edu.uinsight.analytics.centraltendency.calculator;//librerías de Java

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Célula B1 — Medidas de tendencia central.
 * Componente de cálculo puro: recibe la lista de calificaciones ya obtenida
 * implementado del lado de la célula A6) y devuelve el resultado en el DTO.
 *
 * Internamente se usa BigDecimal para sumar/promediar para evitar errores
 * según el DERCAS que se subio.
 * Casos especiales 
 *   1. Sin datos                -> Result.empty()
 *   2. Un solo dato             -> media = mediana = moda = ese único valor
 *   3. Cantidad par de datos    -> la mediana promedia los dos valores centrales
 *   4. Sin moda representativa  -> todas las calificaciones tienen frecuencia 1
 *   5. Más de una moda          -> conjunto multimodal: se retornan todas las modas,
 *                                  ordenadas de menor a mayor
 */
@Component
public class CentralTendencyCalculator {

    /** Decimales usados para el redondeo interno antes de exponer el Double */
    private static final int SCALE = 2;

    /**
     * Resultado interno del calculo, mientras CentralTendencyResponse no esta
     * disponible.
     */
    public record Result(int sampleSize, Double mean, Double median, List<Double> mode) {

        /** Caso "sin datos": sampleSize:0, mean/median:null, mode:[] (Regla de Negocio #2). */
        public static Result empty() {
            return new Result(0, null, null, Collections.emptyList());
        }
    }

    /**
     * Calcula media, mediana y moda a partir de las calificaciones recibidas.
     * La lista ya debe venir filtrada (solo notas válidas) por quien la obtuvo
     * de la célula A6; este componente no conoce el origen de los datos.
     */
    public Result calculate(List<BigDecimal> calificacionesRecibidas) {

        List<BigDecimal> calificaciones =
                (calificacionesRecibidas == null)
                        ? Collections.emptyList()
                        : calificacionesRecibidas;

        // Caso especial 1: sin datos (Regla de Negocio #2).
        if (calificaciones.isEmpty()) {
            return Result.empty();
        }

        List<BigDecimal> calificacionesOrdenadas =
                new ArrayList<>(calificaciones);

        Collections.sort(calificacionesOrdenadas);

        Double media = calculateMean(calificacionesOrdenadas);
        Double mediana = calculateMedian(calificacionesOrdenadas);
        List<Double> moda = calculateMode(calificacionesOrdenadas);

        return new Result(
                calificacionesOrdenadas.size(),
                media,
                mediana,
                moda
        );
    }

    /**

     * suma de todas las notas dividida entre la cantidad de notas.
     */
    private Double calculateMean(List<BigDecimal> calificacionesOrdenadas) {

        BigDecimal suma = calificacionesOrdenadas.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal media = suma.divide(
                BigDecimal.valueOf(calificacionesOrdenadas.size()),
                SCALE,
                RoundingMode.HALF_UP
        );

        return media.doubleValue();
    }

    /**
     * Mediana sobre la lista YA ORDENADA.
     * Caso especial 2:
     * un solo dato cae en la rama impar y retorna ese unico valor.
     */
    private Double calculateMedian(List<BigDecimal> calificacionesOrdenadas) {

        int cantidadDatos = calificacionesOrdenadas.size();
        int indiceMedio = cantidadDatos / 2;

        BigDecimal mediana;

        if (cantidadDatos % 2 != 0) {//si la cantidad de datos es impar

            // Cantidad impar de datos.
            mediana = calificacionesOrdenadas.get(indiceMedio);//el valor central es la mediana

        } else {//si la cantidad de datos es par

            // Cantidad par de datos:
            // promedio de los dos valores centrales.

            BigDecimal valorInferior =
                    calificacionesOrdenadas.get(indiceMedio - 1);

            BigDecimal valorSuperior =
                    calificacionesOrdenadas.get(indiceMedio);

            mediana = valorInferior
                    .add(valorSuperior)
                    .divide(
                            BigDecimal.valueOf(2),
                            SCALE,
                            RoundingMode.HALF_UP
                    );
        }

        return mediana//redondea a SCALE decimales y lo convierte a Double
                .setScale(SCALE, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Moda sobre la lista YA ORDENADA.
     *
     * Caso especial 2:
     * un solo dato -> ese dato es la moda.
     *
     * Caso especial 4:
     * ningún valor se repite -> lista vacía.
     *
     * Caso especial 5:
     * dos o más valores comparten la frecuencia maxiima -> se retornan todos.
     */
    private List<Double> calculateMode(//solo se llama si hay al menos un dato
            List<BigDecimal> calificacionesOrdenadas) {

        if (calificacionesOrdenadas.size() == 1) {

            List<Double> modaUnica = new ArrayList<>();

            modaUnica.add(
                    calificacionesOrdenadas
                            .get(0)
                            .setScale(SCALE, RoundingMode.HALF_UP)
                            .doubleValue()
            );

            return modaUnica;
        }

        Map<BigDecimal, Long> frecuenciaPorValor =//se obtiene un mapa de cada valor y su frecuencia
                calificacionesOrdenadas.stream()
                        .collect(
                                Collectors.groupingBy(
                                        valor -> valor,
                                        LinkedHashMap::new,
                                        Collectors.counting()
                                )
                        );

        long frecuenciaMaxima =
                Collections.max(frecuenciaPorValor.values());

        if (frecuenciaMaxima == 1) {//si la frecuencia máxima es 1, significa que todos los valores son unicos

            // Ninguna nota se repite:
            // no existe una moda representativa.
            return Collections.emptyList();
        }

        return frecuenciaPorValor.entrySet()//retorna una lista de modas ordenadas de menor a mayor, redondeadas a SCALE decimales y convertidas a Double
                .stream()
                .filter(entrada ->
                        entrada.getValue() == frecuenciaMaxima)
                .map(Map.Entry::getKey)
                .sorted()
                .map(valor ->
                        valor.setScale(
                                        SCALE,
                                        RoundingMode.HALF_UP
                                )
                                .doubleValue()
                )
                .collect(Collectors.toList());
    }
}//fin CentralTendencyCalculator