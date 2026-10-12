package gt.edu.uinsight.analytics.dispersion.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gt.edu.uinsight.analytics.dispersion.calculator.DispersionCalculator;
import gt.edu.uinsight.analytics.dispersion.config.DispersionClassifier;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.exception.CursoNoEncontradoException;
import gt.edu.uinsight.analytics.dispersion.exception.DispersionDatosInvalidosException;
import gt.edu.uinsight.analytics.dispersion.exception.SeccionNoEncontradaException;
import gt.edu.uinsight.analytics.dispersion.mapper.DispersionMapper;
import gt.edu.uinsight.analytics.dispersion.repository.DispersionGradeRepository;
import gt.edu.uinsight.section.entity.Section;
import gt.edu.uinsight.section.repository.SectionRepository;

@ExtendWith(MockitoExtension.class)
class DispersionServiceTest {

    @Mock
    private DispersionGradeRepository dispersionGradeRepository;

    @Mock
    private DispersionCalculator dispersionCalculator;

    @Mock
    private DispersionClassifier dispersionClassifier;

    @Mock
    private DispersionMapper dispersionMapper;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private DispersionService service;

    @Test
    void calculaYMapeaLaDispersionDeUnaSeccion() {
        Long sectionId = 1L;

        List<BigDecimal> scores = List.of(
                new BigDecimal("60"),
                new BigDecimal("80")
        );

        BigDecimal min = new BigDecimal("60");
        BigDecimal max = new BigDecimal("80");
        BigDecimal range = new BigDecimal("20");
        BigDecimal variance = new BigDecimal("100");
        BigDecimal standardDeviation = new BigDecimal("10");

        DispersionClassification classification =
                DispersionClassification.MODERATE_DISPERSION;

        when(sectionRepository.existsById(sectionId)).thenReturn(true);
        when(dispersionGradeRepository.findScoresBySectionId(sectionId))
                .thenReturn(scores);
        when(dispersionCalculator.calculateMin(scores)).thenReturn(min);
        when(dispersionCalculator.calculateMax(scores)).thenReturn(max);
        when(dispersionCalculator.calculateRange(scores)).thenReturn(range);
        when(dispersionCalculator.calculateVariance(scores)).thenReturn(variance);
        when(dispersionCalculator.calculateStandardDeviation(scores))
                .thenReturn(standardDeviation);
        when(dispersionClassifier.clasificar(standardDeviation))
                .thenReturn(classification);

        DispersionResponse expectedResponse = mock(DispersionResponse.class);

        when(dispersionMapper.toSectionResponse(
                sectionId, min, max, range, variance,
                standardDeviation, classification
        )).thenReturn(expectedResponse);

        DispersionResponse response = service.getSectionDispersion(sectionId);

        assertNotNull(response);
        verify(dispersionMapper).toSectionResponse(
                sectionId, min, max, range, variance,
                standardDeviation, classification
        );
    }

    @Test
    void calculaYMapeaLaDispersionDeUnCurso() {
        Long courseId = 2L;
        Section section = mock(Section.class);

        List<BigDecimal> scores = List.of(
                new BigDecimal("70"),
                new BigDecimal("90")
        );

        BigDecimal min = new BigDecimal("70");
        BigDecimal max = new BigDecimal("90");
        BigDecimal range = new BigDecimal("20");
        BigDecimal variance = new BigDecimal("100");
        BigDecimal standardDeviation = new BigDecimal("10");

        DispersionClassification classification =
                DispersionClassification.MODERATE_DISPERSION;

        when(sectionRepository.findByCourseId(courseId))
                .thenReturn(List.of(section));
        when(dispersionGradeRepository.findScoresByCourseId(courseId))
                .thenReturn(scores);
        when(dispersionCalculator.calculateMin(scores)).thenReturn(min);
        when(dispersionCalculator.calculateMax(scores)).thenReturn(max);
        when(dispersionCalculator.calculateRange(scores)).thenReturn(range);
        when(dispersionCalculator.calculateVariance(scores)).thenReturn(variance);
        when(dispersionCalculator.calculateStandardDeviation(scores))
                .thenReturn(standardDeviation);
        when(dispersionClassifier.clasificar(standardDeviation))
                .thenReturn(classification);

        DispersionResponse expectedResponse = mock(DispersionResponse.class);

        when(dispersionMapper.toCourseResponse(
                courseId, min, max, range, variance,
                standardDeviation, classification
        )).thenReturn(expectedResponse);

        DispersionResponse response = service.getCourseDispersion(courseId);

        assertNotNull(response);
        verify(dispersionMapper).toCourseResponse(
                courseId, min, max, range, variance,
                standardDeviation, classification
        );
    }

    @Test
    void lanzaExcepcionSiLaSeccionNoExiste() {
        Long sectionId = 99L;

        when(sectionRepository.existsById(sectionId)).thenReturn(false);

        assertThrows(
                SeccionNoEncontradaException.class,
                () -> service.getSectionDispersion(sectionId)
        );
    }

    @Test
    void lanzaExcepcionSiElCursoNoTieneSecciones() {
        Long courseId = 99L;

        when(sectionRepository.findByCourseId(courseId))
                .thenReturn(List.of());

        assertThrows(
                CursoNoEncontradoException.class,
                () -> service.getCourseDispersion(courseId)
        );
    }

    @Test
    void rechazaIdDeSeccionNulo() {
        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> service.getSectionDispersion(null)
        );
    }

    @Test
    void rechazaIdDeSeccionNegativo() {
        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> service.getSectionDispersion(-1L)
        );
    }

    @Test
    void rechazaIdDeCursoNulo() {
        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> service.getCourseDispersion(null)
        );
    }

    @Test
    void rechazaIdDeCursoNegativo() {
        assertThrows(
                DispersionDatosInvalidosException.class,
                () -> service.getCourseDispersion(-1L)
        );
    }
}
