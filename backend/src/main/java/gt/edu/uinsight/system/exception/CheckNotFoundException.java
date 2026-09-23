package gt.edu.uinsight.system.exception;

public class CheckNotFoundException extends RuntimeException {

    public CheckNotFoundException(Long id) {
        super("System check with id " + id + " not found");
    }
}
