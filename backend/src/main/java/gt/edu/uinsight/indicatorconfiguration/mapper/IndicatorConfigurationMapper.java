package gt.edu.uinsight.indicatorconfiguration.mapper;

import gt.edu.uinsight.indicatorconfiguration.dto.request.CreateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.response.IndicatorConfigurationResponse;
import gt.edu.uinsight.indicatorconfiguration.entity.IndicatorConfiguration;
import org.springframework.stereotype.Component;

@Component
public class IndicatorConfigurationMapper {

    public IndicatorConfiguration toEntity(CreateIndicatorConfigurationRequest request) {
        IndicatorConfiguration entity = new IndicatorConfiguration();
        entity.setKey(request.getKey());
        entity.setValue(request.getValue());
        entity.setDescription(request.getDescription());
        return entity;
    }

    public IndicatorConfigurationResponse toResponse(IndicatorConfiguration entity) {
        return new IndicatorConfigurationResponse(
                entity.getId(),
                entity.getKey(),
                entity.getValue(),
                entity.getDescription(),
                entity.getUpdatedAt()
        );
    }
}
