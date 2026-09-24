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
        LOGGER.info("event=OPERATION_STARTED operation=createIndicatorConfiguration key={}", request.getKey());

        if (repository.existsByKey(request.getKey())) {
            LOGGER.warn(
                    "event=BUSINESS_RULE_REJECTED operation=createIndicatorConfiguration rule=UNIQUE_KEY key={}",
                    request.getKey()
            );
            throw new IndicatorConfigurationConflictException(request.getKey());
        }

        IndicatorConfiguration entity = mapper.toEntity(request);
        IndicatorConfiguration saved = repository.save(entity);

        LOGGER.info(
                "event=OPERATION_SUCCEEDED operation=createIndicatorConfiguration key={} id={}",
                saved.getKey(),
                saved.getId()
        );

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndicatorConfigurationResponse> findAll() {
        LOGGER.info("event=OPERATION_STARTED operation=findAllIndicatorConfigurations");

        List<IndicatorConfigurationResponse> response = repository.findAll(Sort.by(Sort.Direction.ASC, "key"))
                .stream()
                .map(mapper::toResponse)
                .toList();

        LOGGER.info(
                "event=OPERATION_SUCCEEDED operation=findAllIndicatorConfigurations count={}",
                response.size()
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public IndicatorConfigurationResponse findByKey(String key) {
        LOGGER.info("event=OPERATION_STARTED operation=findIndicatorConfigurationByKey key={}", key);

        IndicatorConfigurationResponse response = mapper.toResponse(getExisting(key));

        LOGGER.info("event=OPERATION_SUCCEEDED operation=findIndicatorConfigurationByKey key={}", key);
        return response;
    }

    @Override
    @Transactional
    public IndicatorConfigurationResponse update(String key, UpdateIndicatorConfigurationRequest request) {
        LOGGER.info("event=OPERATION_STARTED operation=updateIndicatorConfiguration key={}", key);

        validateUpdateRequest(request, key);

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

        LOGGER.info("event=OPERATION_SUCCEEDED operation=updateIndicatorConfiguration key={}", saved.getKey());

        return mapper.toResponse(saved);
    }

    private IndicatorConfiguration getExisting(String key) {
        return repository.findByKey(key)
                .orElseThrow(() -> {
                    LOGGER.warn(
                            "event=OPERATION_ERROR operation=findIndicatorConfiguration key={} error=NOT_FOUND",
                            key
                    );
                    return new IndicatorConfigurationNotFoundException(key);
                });
    }

    private void validateUpdateRequest(UpdateIndicatorConfigurationRequest request, String key) {
        if (request.getValue() == null && request.getDescription() == null) {
            LOGGER.warn(
                    "event=BUSINESS_RULE_REJECTED operation=updateIndicatorConfiguration key={} rule=EMPTY_UPDATE",
                    key
            );
            throw new IndicatorConfigurationBadRequestException(
                    "Debe enviar al menos value o description para actualizar la configuracion"
            );
        }

        if (request.getValue() != null && request.getValue().isBlank()) {
            LOGGER.warn(
                    "event=BUSINESS_RULE_REJECTED operation=updateIndicatorConfiguration key={} rule=BLANK_VALUE",
                    key
            );
            throw new IndicatorConfigurationBadRequestException("El valor no puede estar vacio");
        }
    }
}
