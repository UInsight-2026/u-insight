package gt.edu.uinsight.analytics.position.service;

import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.exception.AmbiguousSectionException;
import gt.edu.uinsight.analytics.position.exception.InvalidPercentileException;
import gt.edu.uinsight.analytics.position.exception.NoGradesAvailableException;
import gt.edu.uinsight.analytics.position.exception.PositionNotFoundException;
import org.springframework.stereotype.Service;

// Importaciones nuevas para los logs
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PositionService {

    private static final Logger log = LoggerFactory.getLogger(PositionService.class);
    
    // Inyección de la dependencia (Integración con A6)
    private final GradeIntegrationService gradeIntegrationService;

    public PositionService(GradeIntegrationService gradeIntegrationService) {
        this.gradeIntegrationService = gradeIntegrationService;
    }

    public SectionPositionResponse getSectionPosition(Long sectionId, List<Integer> requestedPercentiles) {
        log.info("Iniciando calculo de posiciones para la seccion ID: {}", sectionId);

        if (sectionId == null || sectionId <= 0) {
            log.error("Regla de negocio rechazada: El ID de la seccion {} es invalido", sectionId);
            throw new PositionNotFoundException("No se encontro la seccion con id: " + sectionId);
        }

        // AQUI ESTA LA INTEGRACION: Pedimos los datos al servicio externo en lugar de usar datos estaticos
        List<Double> notasReales = gradeIntegrationService.getGradesBySection(sectionId);
        if (notasReales == null || notasReales.isEmpty()) {
            log.error("Regla de negocio rechazada: la seccion {} no tiene notas registradas", sectionId);
            throw new NoGradesAvailableException("No hay notas registradas para la seccion con id: " + sectionId);
        }

        List<Double> ordenados = new ArrayList<>(notasReales);
        Collections.sort(ordenados);

        Map<String, Double> quartiles = new LinkedHashMap<>();
        quartiles.put("Q1", calcularFractila(ordenados, 4, 1));
        quartiles.put("Q2", calcularFractila(ordenados, 4, 2));
        quartiles.put("Q3", calcularFractila(ordenados, 4, 3));

        Map<String, Double> percentiles = null;
        if (requestedPercentiles != null && !requestedPercentiles.isEmpty()) {
            percentiles = new LinkedHashMap<>();
            for (Integer p : requestedPercentiles) {
                if (p == null || p < 1 || p > 99) {
                    log.error("Regla de negocio rechazada: el percentil {} es invalido", p);
                    throw new InvalidPercentileException(
                            "El percentil " + p + " es invalido. Debe estar entre 1 y 99.");
                }
                percentiles.put("P" + p, calcularFractila(ordenados, 100, p));
            }
        }

        log.info("Calculo exitoso de medidas de posicion para la seccion ID: {}", sectionId);
        return new SectionPositionResponse(sectionId, ordenados.size(), quartiles, percentiles);
    }

    /**
     * Sobrecarga de compatibilidad para llamadores existentes (p.ej. B5) que
     * todavia no pasan sectionId explicito.
     */
    public StudentPositionResponse getStudentPosition(Long studentId) {
        return getStudentPosition(studentId, null);
    }

    public StudentPositionResponse getStudentPosition(Long studentId, Long sectionId) {
        log.info("Iniciando calculo de posicion individual para el estudiante ID: {}", studentId);

        if (studentId == null || studentId <= 0) {
            log.error("Regla de negocio rechazada: El ID del estudiante {} es invalido", studentId);
            throw new PositionNotFoundException("No se encontro el estudiante con id: " + studentId);
        }

        // AQUI ESTA LA INTEGRACION: Usamos el servicio externo para obtener las notas propias del estudiante
        List<Double> notasEstudiante = gradeIntegrationService.getGradesByStudent(studentId);
        if (notasEstudiante == null || notasEstudiante.isEmpty()) {
            log.error("Regla de negocio rechazada: el estudiante {} no tiene notas registradas", studentId);
            throw new NoGradesAvailableException("No hay notas registradas para el estudiante con id: " + studentId);
        }

        String studentCode = "EST-%04d".formatted(studentId);
        double studentAverage = notasEstudiante.stream().mapToDouble(Double::doubleValue).average().orElseThrow();

        // El percentil se calcula contra TODA la seccion del estudiante, no contra sus propias notas
        Long resolvedSectionId = resolveSectionId(studentId, sectionId);
        List<Double> notasSeccion = gradeIntegrationService.getGradesBySection(resolvedSectionId);
        if (notasSeccion == null || notasSeccion.isEmpty()) {
            log.error("Regla de negocio rechazada: la seccion {} del estudiante {} no tiene notas registradas",
                    resolvedSectionId, studentId);
            throw new NoGradesAvailableException(
                    "No hay notas registradas para la seccion del estudiante con id: " + studentId);
        }
        List<Double> ordenados = new ArrayList<>(notasSeccion);
        Collections.sort(ordenados);

        int percentile = calcularPercentilDeValor(ordenados, studentAverage);

        log.info("Calculo exitoso de percentil para el estudiante ID: {}", studentId);
        return new StudentPositionResponse(studentCode, studentAverage, percentile);
    }

    /**
     * Resuelve la seccion contra la que se debe comparar al estudiante.
     *
     * Un estudiante puede estar matriculado en mas de una seccion (una por
     * curso), por lo que no se puede asumir una unica seccion "del"
     * estudiante. Si el cliente especifica sectionId se valida la matricula;
     * si no lo especifica, solo se resuelve automaticamente cuando el
     * estudiante tiene una unica seccion -- de lo contrario se exige que el
     * cliente la especifique explicitamente.
     */
    private Long resolveSectionId(Long studentId, Long sectionId) {
        if (sectionId != null) {
            if (sectionId <= 0 || !gradeIntegrationService.isStudentEnrolledInSection(studentId, sectionId)) {
                log.error("Regla de negocio rechazada: el estudiante {} no esta matriculado en la seccion {}",
                        studentId, sectionId);
                throw new PositionNotFoundException(
                        "El estudiante con id: " + studentId + " no esta matriculado en la seccion con id: " + sectionId);
            }
            return sectionId;
        }

        List<Long> secciones = gradeIntegrationService.getSectionIdsByStudent(studentId);
        if (secciones == null || secciones.isEmpty()) {
            log.error("Regla de negocio rechazada: el estudiante {} no esta matriculado en ninguna seccion", studentId);
            throw new PositionNotFoundException(
                    "El estudiante con id: " + studentId + " no esta matriculado en ninguna seccion");
        }
        if (secciones.size() > 1) {
            log.error("Regla de negocio rechazada: el estudiante {} esta matriculado en {} secciones, se requiere sectionId",
                    studentId, secciones.size());
            throw new AmbiguousSectionException(
                    "El estudiante con id: " + studentId + " esta matriculado en mas de una seccion ("
                            + secciones.size() + "). Especifique el parametro sectionId.");
        }
        return secciones.get(0);
    }

    /**
     * Calcula una medida de posicion (cuartil, decil o percentil) para datos NO agrupados.
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
     * Calcula el percentil (0-100) que ocupa un valor dado dentro de un conjunto ordenado.
     */
    private int calcularPercentilDeValor(List<Double> ordenados, double valor) {
        long cantidadMenoresOIguales = ordenados.stream().filter(v -> v <= valor).count();
        return (int) Math.round((cantidadMenoresOIguales * 100.0) / ordenados.size());
    }
}