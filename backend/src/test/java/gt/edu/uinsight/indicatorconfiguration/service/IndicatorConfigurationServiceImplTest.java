package gt.edu.uinsight.indicatorconfiguration.service;

import gt.edu.uinsight.indicatorconfiguration.dto.request.CreateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.request.UpdateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.response.IndicatorConfigurationResponse;
import gt.edu.uinsight.indicatorconfiguration.entity.IndicatorConfiguration;
import gt.edu.uinsight.indicatorconfiguration.exception.IndicatorConfigurationBadRequestException;
import gt.edu.uinsight.indicatorconfiguration.exception.IndicatorConfigurationConflictException;
import gt.edu.uinsight.indicatorconfiguration.exception.IndicatorConfigurationNotFoundException;
import gt.edu.uinsight.indicatorconfiguration.mapper.IndicatorConfigurationMapper;
import gt.edu.uinsight.indicatorconfiguration.repository.IndicatorConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicatorConfigurationServiceImplTest {

    @Mock
    private IndicatorConfigurationRepository repository;

    @Mock
    private IndicatorConfigurationMapper mapper;

    private IndicatorConfigurationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new IndicatorConfigurationServiceImpl(repository, mapper);
    }

    @Test
    void create_debeCrearConfiguracion_cuandoLaClaveNoExiste() {
        CreateIndicatorConfigurationRequest request = createRequest(
                "HIGH_RISK_PERCENTAGE",
                "50",
                "Porcentaje de riesgo"
        );

        IndicatorConfiguration entity = entity(null, "HIGH_RISK_PERCENTAGE", "50", "Porcentaje de riesgo");
        IndicatorConfiguration saved = entity(1L, "HIGH_RISK_PERCENTAGE", "50", "Porcentaje de riesgo");
        IndicatorConfigurationResponse expected = response(saved);

        when(repository.existsByKey("HIGH_RISK_PERCENTAGE")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(expected);

        IndicatorConfigurationResponse actual = service.create(request);

        assertEquals(expected, actual);
        verify(repository).save(entity);
    }

    @Test
    void create_debeRechazarConfiguracion_cuandoLaClaveYaExiste() {
        CreateIndicatorConfigurationRequest request = createRequest(
                "HIGH_RISK_PERCENTAGE",
                "50",
                null
        );

        when(repository.existsByKey("HIGH_RISK_PERCENTAGE")).thenReturn(true);

        assertThrows(IndicatorConfigurationConflictException.class, () -> service.create(request));

        verify(repository, never()).save(any(IndicatorConfiguration.class));
    }

    @Test
    void findAll_debeRetornarConfiguracionesOrdenadas() {
        IndicatorConfiguration first = entity(1L, "HIGH_RISK_PERCENTAGE", "50", "Riesgo alto");
        IndicatorConfiguration second = entity(2L, "LOW_PERFORMANCE_THRESHOLD", "61", "Rendimiento bajo");

        IndicatorConfigurationResponse firstResponse = response(first);
        IndicatorConfigurationResponse secondResponse = response(second);

        when(repository.findAll(any(Sort.class))).thenReturn(List.of(first, second));
        when(mapper.toResponse(first)).thenReturn(firstResponse);
        when(mapper.toResponse(second)).thenReturn(secondResponse);

        List<IndicatorConfigurationResponse> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(firstResponse, result.get(0));
        assertEquals(secondResponse, result.get(1));
    }

    @Test
    void findByKey_debeRetornarConfiguracion_cuandoExiste() {
        IndicatorConfiguration entity = entity(1L, "NEGATIVE_CHANGE_THRESHOLD", "-3", "Cambio negativo");
        IndicatorConfigurationResponse expected = response(entity);

        when(repository.findByKey("NEGATIVE_CHANGE_THRESHOLD")).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(expected);

        IndicatorConfigurationResponse actual = service.findByKey("NEGATIVE_CHANGE_THRESHOLD");

        assertEquals(expected, actual);
    }

    @Test
    void findByKey_debeLanzarExcepcion_cuandoNoExiste() {
        when(repository.findByKey("DOES_NOT_EXIST")).thenReturn(Optional.empty());

        assertThrows(
                IndicatorConfigurationNotFoundException.class,
                () -> service.findByKey("DOES_NOT_EXIST")
        );
    }

    @Test
    void update_debeActualizarValorYDescripcion_cuandoLaConfiguracionExiste() {
        UpdateIndicatorConfigurationRequest request = new UpdateIndicatorConfigurationRequest();
        request.setValue("55");
        request.setDescription("Valor actualizado");

        IndicatorConfiguration entity = entity(1L, "HIGH_RISK_PERCENTAGE", "50", "Valor inicial");
        IndicatorConfigurationResponse expected = new IndicatorConfigurationResponse(
                1L,
                "HIGH_RISK_PERCENTAGE",
                "55",
                "Valor actualizado",
                entity.getUpdatedAt()
        );

        when(repository.findByKey("HIGH_RISK_PERCENTAGE")).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(expected);

        IndicatorConfigurationResponse actual = service.update("HIGH_RISK_PERCENTAGE", request);

        assertEquals("55", entity.getValue());
        assertEquals("Valor actualizado", entity.getDescription());
        assertEquals(expected, actual);
        verify(repository).save(entity);
    }

    @Test
    void update_debeRechazarSolicitud_cuandoNoIncluyeCampos() {
        UpdateIndicatorConfigurationRequest request = new UpdateIndicatorConfigurationRequest();

        assertThrows(
                IndicatorConfigurationBadRequestException.class,
                () -> service.update("HIGH_RISK_PERCENTAGE", request)
        );

        verify(repository, never()).findByKey(any(String.class));
    }

    @Test
    void update_debeRechazarValor_cuandoEstaVacio() {
        UpdateIndicatorConfigurationRequest request = new UpdateIndicatorConfigurationRequest();
        request.setValue("   ");

        assertThrows(
                IndicatorConfigurationBadRequestException.class,
                () -> service.update("HIGH_RISK_PERCENTAGE", request)
        );

        verify(repository, never()).save(any(IndicatorConfiguration.class));
    }

    private CreateIndicatorConfigurationRequest createRequest(String key, String value, String description) {
        CreateIndicatorConfigurationRequest request = new CreateIndicatorConfigurationRequest();
        request.setKey(key);
        request.setValue(value);
        request.setDescription(description);
        return request;
    }

    private IndicatorConfiguration entity(Long id, String key, String value, String description) {
        IndicatorConfiguration entity = new IndicatorConfiguration();
        entity.setId(id);
        entity.setKey(key);
        entity.setValue(value);
        entity.setDescription(description);
        entity.setUpdatedAt(LocalDateTime.of(2026, 9, 23, 20, 0));
        return entity;
    }

    private IndicatorConfigurationResponse response(IndicatorConfiguration entity) {
        return new IndicatorConfigurationResponse(
                entity.getId(),
                entity.getKey(),
                entity.getValue(),
                entity.getDescription(),
                entity.getUpdatedAt()
        );
    }
}
