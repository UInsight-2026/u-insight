package gt.edu.uinsight.common.exception;

/** Se lanza cuando una operacion viola una regla de negocio del modulo (HTTP 409). */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
