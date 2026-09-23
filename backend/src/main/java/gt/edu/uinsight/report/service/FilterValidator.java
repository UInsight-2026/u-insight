package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.exception.InvalidFilterException;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class FilterValidator {

    private static final Set<String> VALID_RISK_LEVELS = Set.of("LOW", "MEDIUM", "HIGH");

    public void validate(ReportFilter filter) {
        if (filter == null) {
            return;
        }
        validateRiskLevel(filter.getRiskLevel());

    }

    private void validateRiskLevel(String riskLevel) {
        if (riskLevel != null && !riskLevel.isBlank() && !VALID_RISK_LEVELS.contains(riskLevel.toUpperCase())) {
            throw new InvalidFilterException(
                    "riskLevel invalido: '" + riskLevel + "'. Valores permitidos: LOW, MEDIUM, HIGH.");
        }
    }
}
