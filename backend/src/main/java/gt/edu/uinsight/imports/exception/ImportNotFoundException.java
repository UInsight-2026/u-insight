package gt.edu.uinsight.imports.exception;

public class ImportNotFoundException extends RuntimeException {

    public ImportNotFoundException(Long importId) {
        super("No se encontró la importación con id " + importId);
    }
}