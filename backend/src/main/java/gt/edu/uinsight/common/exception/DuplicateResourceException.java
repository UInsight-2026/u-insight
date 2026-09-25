package gt.edu.uinsight.common.exception;

/** Se lanza cuando se intenta duplicar un valor unico, como el codigo de docente (HTTP 409). */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
