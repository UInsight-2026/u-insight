package gt.edu.uinsight.academicperiod.support.exception;

import org.springframework.http.HttpStatus;

/**
 * Regla de negocio de la celula A1 rechazada. Cada instancia lleva el identificador
 * de la regla (RN-01..RN-09) y el codigo HTTP con el que se responde:
 * <ul>
 *   <li>400: RN-02 (fechas), RN-08 (creditos) y valores de estado no reconocidos.</li>
 *   <li>409: RN-01 y RN-07 (duplicados), RN-03 (un solo periodo activo),
 *       RN-04 (periodo cerrado) y RN-06 (transicion de estado invalida).</li>
 * </ul>
 */
public class AcademicBusinessRuleException extends RuntimeException {

    private final String ruleId;
    private final HttpStatus status;

    private AcademicBusinessRuleException(String ruleId, HttpStatus status, String message) {
        super(message);
        this.ruleId = ruleId;
        this.status = status;
    }

    /** Regla rechazada por datos invalidos: responde 400. */
    public static AcademicBusinessRuleException badRequest(String ruleId, String message) {
        return new AcademicBusinessRuleException(ruleId, HttpStatus.BAD_REQUEST, message);
    }

    /** Regla rechazada por conflicto con el estado actual: responde 409. */
    public static AcademicBusinessRuleException conflict(String ruleId, String message) {
        return new AcademicBusinessRuleException(ruleId, HttpStatus.CONFLICT, message);
    }

    /** Regla de unicidad (RN-01, RN-07) rechazada: responde 409. */
    public static AcademicBusinessRuleException duplicate(String ruleId, String message) {
        return new AcademicBusinessRuleException(ruleId, HttpStatus.CONFLICT, message);
    }

    /** Valor de entrada no reconocido (por ejemplo un estado inexistente): responde 400. */
    public static AcademicBusinessRuleException invalidValue(String message) {
        return new AcademicBusinessRuleException(null, HttpStatus.BAD_REQUEST, message);
    }

    public String getRuleId() {
        return ruleId;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public boolean isDuplicate() {
        return "RN-01".equals(ruleId) || "RN-07".equals(ruleId);
    }
}
