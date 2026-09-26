//SEMANA 3
package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.PageFilter;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;


@Component
public class FilterValidator {

    private static final Set<String> VALID_RISK_LEVELS =
            Set.of("LOW", "MEDIUM", "HIGH");

    // Ciclo de vida oficial de una alerta (documento maestro, célula C3).
    private static final Set<String> VALID_ALERT_STATUSES =
            Set.of("NEW", "UNDER_REVIEW", "IN_PROGRESS", "RESOLVED", "DISMISSED");

    private static final Set<String> VALID_SORTS =
            Set.of("NEWEST", "OLDEST", "RISK");

    // Período académico: cuatro dígitos de año, guión, y 1 o 2. Ej. 2026-2
    private static final Pattern PERIOD_PATTERN = Pattern.compile("\\d{4}-[12]");

    public void validate(ReportFilter filter) {
        if (filter == null) {
            return;
        }
        validateRiskLevel(filter.getRiskLevel());
        validateAlertStatus(filter.getAlertStatus());
        validatePeriod(filter.getPeriod());
    }

    public void validatePage(PageFilter pageFilter) {
        if (pageFilter == null) {
            return;
        }
        if (pageFilter.getPage() < 0) {
            throw new InvalidFilterException(
                    "page invalido: " + pageFilter.getPage() + ". Debe ser 0 o mayor.");
        }
        if (pageFilter.getSize() < 1 || pageFilter.getSize() > PageFilter.MAX_SIZE) {
            throw new InvalidFilterException(
                    "size invalido: " + pageFilter.getSize()
                            + ". Debe estar entre 1 y " + PageFilter.MAX_SIZE + ".");
        }
        if (!VALID_SORTS.contains(pageFilter.getSort())) {
            throw new InvalidFilterException(
                    "sort invalido: '" + pageFilter.getSort()
                            + "'. Valores permitidos: NEWEST, OLDEST, RISK.");
        }
    }

    private void validateRiskLevel(String riskLevel) {
        if (isBlank(riskLevel)) {
            return;
        }
        if (!VALID_RISK_LEVELS.contains(riskLevel.toUpperCase())) {
            throw new InvalidFilterException(
                    "riskLevel invalido: '" + riskLevel + "'. Valores permitidos: LOW, MEDIUM, HIGH.");
        }
    }

    private void validateAlertStatus(String alertStatus) {
        if (isBlank(alertStatus)) {
            return;
        }
        if (!VALID_ALERT_STATUSES.contains(alertStatus.toUpperCase())) {
            throw new InvalidFilterException(
                    "alertStatus invalido: '" + alertStatus + "'. Valores permitidos: "
                            + "NEW, UNDER_REVIEW, IN_PROGRESS, RESOLVED, DISMISSED.");
        }
    }

    private void validatePeriod(String period) {
        if (isBlank(period)) {
            return;
        }
        if (!PERIOD_PATTERN.matcher(period).matches()) {
            throw new InvalidFilterException(
                    "period invalido: '" + period + "'. Formato esperado: AAAA-N, por ejemplo 2026-2.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}