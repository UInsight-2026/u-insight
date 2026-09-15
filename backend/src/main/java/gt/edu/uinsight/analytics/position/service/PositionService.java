package gt.edu.uinsight.analytics.position.service;

import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.exception.PositionNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PositionService {

    // Dato simulado temporalmente, mientras A6 (calificaciones) no tiene su API lista.
    // Cuando exista, aqui se reemplazara por la consulta real de notas de la seccion.
    private static final List<Double> MOCK_GRADES =
            Arrays.asList(60.0, 72.0, 85.0, 90.0, 55.0, 78.0, 88.0, 92.0, 67.0, 74.0);

    public SectionPositionResponse getSectionPosition(Long sectionId, List<Integer> requestedPercentiles) {
        if (sectionId == null || sectionId <= 0) {
            throw new PositionNotFoundException("No se encontro la seccion con id: " + sectionId);
        }

        List<Double> ordenados = new ArrayList<>(MOCK_GRADES);
        Collections.sort(ordenados);

        Map<String, Double> quartiles = new LinkedHashMap<>();
        quartiles.put("Q1", calcularFractila(ordenados, 4, 1));
        quartiles.put("Q2", calcularFractila(ordenados, 4, 2));
        quartiles.put("Q3", calcularFractila(ordenados, 4, 3));

        Map<String, Double> percentiles = null;
        if (requestedPercentiles != null && !requestedPercentiles.isEmpty()) {
            percentiles = new LinkedHashMap<>();
            for (Integer p : requestedPercentiles) {
                percentiles.put("P" + p, calcularFractila(ordenados, 100, p));
            }
        }

        return new SectionPositionResponse(sectionId, ordenados.size(), quartiles, percentiles);
    }

    public StudentPositionResponse getStudentPosition(Long studentId) {
        if (studentId == null || studentId <= 0) {
            throw new PositionNotFoundException("No se encontro el estudiante con id: " + studentId);
        }

        List<Double> ordenados = new ArrayList<>(MOCK_GRADES);
        Collections.sort(ordenados);

        String studentCode = "EST-%04d".formatted(studentId);
        double studentAverage = 58.0;

        int percentile = calcularPercentilDeValor(ordenados, studentAverage);

        return new StudentPositionResponse(studentCode, studentAverage, percentile);
    }

    /**
     * Calcula una medida de posicion (cuartil, decil o percentil) para datos NO agrupados.
     *
     * Formula:  posicion = (j * n) / k + 1/2
     * Si la posicion resultante no es un numero entero, se interpola entre
     * el dato en la posicion inferior (Pn) y el dato en la posicion superior (Pn+1):
     *
     *   I = Pn + Fp * (Pn+1 - Pn)
     *
     * donde Fp es la parte decimal de la posicion.
     */
    private double calcularFractila(List<Double> ordenados, int k, int j) {
        int n = ordenados.size();
        double posicion = ((double) (j * n) / k) + 0.5;
        return interpolar(ordenados, posicion);
    }

    private double interpolar(List<Double> ordenados, double posicion) {
        int n = ordenados.size();

        if (posicion <= 1) {
            return ordenados.get(0);
        }
        if (posicion >= n) {
            return ordenados.get(n - 1);
        }

        int posEntera = (int) Math.floor(posicion);
        double parteDecimal = posicion - posEntera;

        double pn = ordenados.get(posEntera - 1);
        double pn1 = ordenados.get(posEntera);

        if (parteDecimal == 0) {
            return pn;
        }

        return pn + parteDecimal * (pn1 - pn);
    }

    /**
     * Calcula el percentil (0-100) que ocupa un valor dado dentro de un conjunto ordenado,
     * como el porcentaje de datos que son menores o iguales a ese valor.
     */
    private int calcularPercentilDeValor(List<Double> ordenados, double valor) {
        long cantidadMenoresOIguales = ordenados.stream().filter(v -> v <= valor).count();
        return (int) Math.round((cantidadMenoresOIguales * 100.0) / ordenados.size());
    }
}
