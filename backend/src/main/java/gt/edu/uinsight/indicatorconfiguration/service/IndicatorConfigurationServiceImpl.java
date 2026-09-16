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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IndicatorConfigurationServiceImpl implements IndicatorConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorConfigurationServiceImpl.class);

    private final IndicatorConfigurationRepository repository;
    private final IndicatorConfigurationMapper mapper;

    public IndicatorConfigurationServiceImpl(
            IndicatorConfigurationRepository repository,
            IndicatorConfigurationMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public IndicatorConfigurationResponse create(CreateIndicatorConfigurationRequest request) {
        if (repository.existsByKey(request.getKey())) {
            throw new IndicatorConfigurationConflictException(request.getKey());
        }

        IndicatorConfiguration entity = mapper.toEntity(request);
        IndicatorConfiguration saved = repository.save(entity);

        LOGGER.info("event=RESOURCE_CREATED resource=IndicatorConfiguration key={}", saved.getKey());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndicatorConfigurationResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "key"))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IndicatorConfigurationResponse findByKey(String key) {
        return mapper.toResponse(getExisting(key));
    }

    @Override
    @Transactional
    public IndicatorConfigurationResponse update(String key, UpdateIndicatorConfigurationRequest request) {
        validateUpdateRequest(request);

        IndicatorConfiguration entity = getExisting(key);
        String previousValue = entity.getValue();

        if (request.getValue() != null) {
            entity.setValue(request.getValue());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }

        IndicatorConfiguration saved = repository.save(entity);

        LOGGER.info(
                "event=INDICATOR_CONFIGURATION_CHANGED key={} previousValue={} newValue={}",
                saved.getKey(),
                previousValue,
                saved.getValue()
        );

        return mapper.toResponse(saved);
    }

    private IndicatorConfiguration getExisting(String key) {
        return repository.findByKey(key)
                .orElseThrow(() -> {
                    LOGGER.warn("event=RESOURCE_NOT_FOUND resource=IndicatorConfiguration key={}", key);
                    return new IndicatorConfigurationNotFoundException(key);
                });
    }

    private void validateUpdateRequest(UpdateIndicatorConfigurationRequest request) {
        if (request.getValue() == null && request.getDescription() == null) {
            throw new IndicatorConfigurationBadRequestException(
                    "Debe enviar al menos value o description para actualizar la configuracion"
            );
        }

        if (request.getValue() != null && request.getValue().isBlank()) {
            throw new IndicatorConfigurationBadRequestException("El valor no puede estar vacio");
        }
    }
}
