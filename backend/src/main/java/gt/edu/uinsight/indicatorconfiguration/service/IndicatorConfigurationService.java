package gt.edu.uinsight.indicatorconfiguration.service;

import gt.edu.uinsight.indicatorconfiguration.dto.request.CreateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.request.UpdateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.response.IndicatorConfigurationResponse;

import java.util.List;

public interface IndicatorConfigurationService {

    IndicatorConfigurationResponse create(CreateIndicatorConfigurationRequest request);

    List<IndicatorConfigurationResponse> findAll();

    IndicatorConfigurationResponse findByKey(String key);

    IndicatorConfigurationResponse update(String key, UpdateIndicatorConfigurationRequest request);
}
